<p align="center">
    <a href="https://app.codacy.com/gh/Fulminazzo/userstalker-minecraft/"><img src="https://fulminazzo.it/badge/code/Fulminazzo/userstalker-minecraft?type=code" alt="Lines of Code" /></a>
    <a href="../../releases/latest"><img src="https://img.shields.io/github/v/release/Fulminazzo/userstalker-minecraft?display_name=tag&color=red" alt="Latest version" /></a>
    <a href="https://app.codacy.com/gh/Fulminazzo/userstalker-minecraft/"><img src="https://fulminazzo.it/badge/code/Fulminazzo/userstalker-minecraft?type=test" alt="Lines of Code" /></a>
</p>
<p align="center">
    <a href="https://github.com/fulminazzo/userstalker-minecraft/actions"><img src="https://fulminazzo.it/badge/coverage/Fulminazzo/userstalker-minecraft/tests.yml" alt="Tests coverage" /></a>
</p>
<p align="center">
    <img src="https://img.shields.io/badge/They%20are-spying%20on%20us-aa0000?style=for-the-badge" alt="" />
</p>

**UserStalker** is a **statistic plugin** created for **Bukkit 1.13+** (it may work on lower versions, but it has not
been tested).

It uses the [UserStalker REST API](https://github.com/fulminazzo/user-stalker) to keep track of every **player access**
in the server and display them in a **GUI format**.

It also provides a **skin cache** system that directly interfaces with the **Mojang API** for displaying
customized player heads.

| **Table of Contents**       |
|-----------------------------|
| [Commands](#commands)       |
| [Skin Cache](#skin-cache)   |
| [API Client](#api-client)   |
| [GUI Manager](#gui-manager) |

The plugin generates four files upon loading:

- [`config.yml`](./blob/master/src/main/resources/config.yml), to configure the plugin behaviour;
- `messages.yml`, to edit all the messages sent by the plugin;
- `skin_cache`, the [skin cache](#skin-cache) (if set to file in the `config.yml`);
- `guis.yml`, to customize the GUIs shown by the [GUI Manager](#gui-manager).

In the following section every module of the plugin will be described, along with the entries in the
`config.yml` required to configure it.

### Commands

The following are all the commands with usages and permissions of the plugin.
All the messages can be edited in the `messages.yml`.

| **Command usage**                 | **Permission**                | **Aliases**               | **Description**                                                                                                                                                                          |
|-----------------------------------|-------------------------------|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/userstalker <subcommand>`       | _userstalker.command_         | `users`, `ustalker`, `us` | The main command of the plugin. Requires a subcommand to be specified.                                                                                                                   |
| `/userstalker reload`             | _userstalker.command.reload_  |                           | Reloads the plugin configuration files and managers. If an error occurs, it is shown in console and the plugin disabled.                                                                 |
| `/userstalker opengui <username>` | _userstalker.command.opengui_ | `open`, `gui`             | Opens the main GUI of the plugin. If a username is specified, a GUI displaying the accesses of that user will be used.                                                                   |
| `/userstalker help <subcommand>`  | _userstalker.command.help_    | `?`                       | Shows description and usage for all the available subcommands (if the issuer has permission). If an argument is specified, all the subcommands that contain that argument will be shown. |

### Skin Cache

When displaying [player heads](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html#PLAYER_HEAD)
with skins, Minecraft queries
the [Mojang API](https://sessionserver.mojang.com/session/minecraft/profile/UUID_HERE?unsigned=false)
to get the texture and signature values and update them for the item.

To avoid sending numerous requests to the endpoint, **UserStalker** provides a **skin cache** to store the results
with an expiry date.

This cache is totally independent of its usage, so it might also be used to set a player's skin (after some rework,
as every request is **synchronous**).

The [`config.yml`](./blob/master/src/main/resources/config.yml) section that the **skin cache** uses for setup is
`skin-cache`:

```yaml
skin-cache:
  type: JSON
  expire-time: 86400
  blacklist-time: 86400
  # Only valid if type = SQL
  address: "localhost"
  database-type: "mysql"
  database: "userstalker"
  username: "sa"
  password: ""
```

- `type`: specifies the type of the cache. Accepted values are `JSON`, `YAML`, `TOML`, `XML` or `SQL`.
  In case the type is `SQL`, the parameters `address`, `database-type`, `database`, `username` and `password`
  will be required. For **everything else**, a _skin_cache_ file with the proper extension will be created
  inside the **plugin folder**;
- `expire-time`: specifies after how much time (in seconds) the cached skin value should expire;
- `blacklist-time`: when querying the API for a user's skin, if it is not present (aka the user is not premium),
  it will enter a **blacklist** to prevent further (useless) queries. In this section it is possible to specify
  after how much time the user should be removed from the blacklist.

### API Client

The **API Client** issues queries to the **UserStalker** REST API endpoints **asynchronously**.

The [`config.yml`](./blob/master/src/main/resources/config.yml) section that it uses for setup is
`userstalker-http-server` (self-explanatory):

```yaml
userstalker-http-server:
  address: "http://localhost"
  port: 8080
```

**NOTE:** when requesting **usernames** to the endpoint (for example, when issuing the
[`/userstalker opengui <username>`](#commands) command), the plugin will **temporarily store**
the results to avoid further queries. 
The cache is updated only upon **plugin reload** or when a **player enters the server**.

Therefore, manual modifications of the **UserStalker database** (using PostMan with the endpoints, 
direct access...) are **highly discouraged**.  

### GUI Manager

The **GUI Manager** is the heart of **UserStalker**: it provides the previously described modules
to retrieve data from the server and displaying it to the players, without halting the server in the process.

Upon first load of the plugin, the manager will create and populate a `guis.yml` file containing all the GUIs
available for customization.

Here is an example of such document with explanations using YAML comments:

```yaml
guis:
  # The main menu shown when issuing /userstalker opengui
  main-menu:
    title: '&cUserStalker Main Menu'
    # The contents specified in this GUI are special.
    # They can have an "action" as variable, which can be one of the following:
    # - open-gui-top : opens the top-users-logins GUI 
    # - open-gui-monthly : opens the monthly-users-logins GUI 
    # - open-gui-newest : opens the newest-users-logins GUI
    # - close : closes this GUI
    contents:
      # ...
      '22':
        - item:
            material: diamond_block
            amount: 1
            display-name: '&bTop users logins'
            lore:
              - '&eShows the number of logins per user,'
              - '&esorted from highest to lowest.'
            custom-model-data: 0
          variables:
            action: open-gui-top
          type: ITEM
      # ...
      '30':
        - item:
            material: emerald_block
            amount: 1
            display-name: '&2Newest users logins'
            lore:
              - '&eShows the latest users logins'
              - '&esorted by newest.'
            custom-model-data: 0
          variables:
            action: open-gui-newest
          type: ITEM
      '32':
        - item:
            material: gold_block
            amount: 1
            display-name: '&6Monthly users logins'
            lore:
              - '&eShows the number of logins per user,'
              - '&esorted from highest to lowest'
              - '&eof the current month.'
            custom-model-data: 0
          variables:
            action: open-gui-monthly
          type: ITEM
      # ...
      '45':
        - item:
            material: barrier
            amount: 1
            display-name: '&cClose'
            lore: []
          variables:
            action: close
          type: ITEM
      # ...
    gui-type: DEFAULT # Can use custom types like CHEST, DISPENSER, ANVIL and more
    size: 54
  # The menu that displays a list of users with their accesses, sorted from highest to lowest 
  top-users-logins:
    title: '&cTop users logins'
    contents: [ ... ]
    type: DATA
    size: 54
    gui-type: DEFAULT # Can use custom types like CHEST, DISPENSER, ANVIL and more
    previous_page:
      slot: 47
      content: { ... }
    next_page:
      slot: 51
      content: { ... }
  # The menu that displays a list of users with their accesses, sorted from highest to lowest, based on the current month
  monthly-users-logins:
    title: '&cMonthly users logins'
    contents: [ ... ]
    type: DATA
    size: 45
    gui-type: DEFAULT # Can use custom types like CHEST, DISPENSER, ANVIL and more
    previous_page:
      slot: 38
      content: { ... }
    next_page:
      slot: 42
      content: { ... }
  # The menu that displays the accesses on the server, sorted from most recent. 
  newest-users-logins:
    title: '&cNewest users logins'
    contents: [ ... ]
    type: DATA
    size: 45
    gui-type: DEFAULT # Can use custom types like CHEST, DISPENSER, ANVIL and more
    previous_page:
      slot: 38
      content: { ... }
    next_page:
      slot: 42
      content: { ... }
  # The menu shown upon clicking on one login entry of the previous GUIs, or
  # when issuing the command /userstalker opengui <username>
  user-logins:
    title: '&c<username>''s logins'
    contents: [ ... ]
    type: DATA
    size: 54
    gui-type: DEFAULT # Can use custom types like CHEST, DISPENSER, ANVIL and more
    previous_page:
      slot: 47
      content: { ... }
    next_page:
      slot: 51
      content: { ... }
items:
  # The item used to display the data entries in the top-users-logins GUI
  # Available variables:
  # - <username> : the username of the user
  # - <login_count> : the number of accesses of the user to the server
  top-users-logins:
    item:
      material: player_head
      display-name: '&fName: &b<username>'
      lore:
        - '&fNumber of accesses: &e<login_count>'
      custom-model-data: 0
    type: ITEM
  # The item used to display the data entries in the monthly-users-logins GUI
  # Available variables:
  # - <username> : the username of the user
  # - <login_count> : the number of accesses of the user to the server
  monthly-users-logins:
    item:
      material: player_head
      display-name: '&fName: &b<username>'
      lore:
        - '&fNumber of accesses: &e<login_count>'
      custom-model-data: 0
    type: ITEM
  # The item used to display the data entries in the newest-users-logins GUI
  # Available variables:
  # - <username> : the username of the user
  # - <ip> : the ip of the user at the time of the login
  # - <login_date> : the time of the login
  newest-users-logins:
    item:
      material: player_head
      display-name: '&fName: &b<username>'
      lore:
        - '&fIp: &c<ip>'
        - '&fLogin date: &a<login_date>'
      custom-model-data: 0
    type: ITEM
  # The item used to display the data entries in the user-logins GUI
  # Available variables:
  # - <username> : the username of the user
  # - <ip> : the ip of the user at the time of the login
  # - <login_date> : the time of the login
  user-logins:
    item:
      material: book
      display-name: '&fIp: &c<ip>'
      lore:
        - '&fLogin date: &a<login_date>'
      custom-model-data: 0
    type: ITEM
  # The item used to go to the previous GUI.
  back:
    item:
      material: barrier
      display-name: '&cBack'
      lore: []
      custom-model-data: 0
    type: ITEM
misc:
  # The offset slot of the back item.
  # The slot is calculated using GUI.size + back.offset for EVERY GUI
  back-offset: -9
```