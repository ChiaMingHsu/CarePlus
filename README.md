# CarePlus

## Setup

### Firebase

1. Project

    1. Register Application
    
        1. package name: com.careplus
        
        1. nickname: CarePlus
        
        1. SHA-1: by doing the following
        
            * Debug
            ```
            $ keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
            ```
            
            * Release
            ```
            $ keytool -exportcert -list -v -alias careplus -keystore careplus.keystore
            ```
            
            > Information of key store
            >   Alias: careplus
            >   Organization: CarePlus
            >   Password: (VSLab default)
            
    2. Download `google-services.json` and put it under `app` folder
             
1. Authentication

    1. Enable `Email/Password` sign-in method
     
    1. Enable `Google` sign-in method 

1. Realtime Database

    1. Add rule
    
        ```json
        {
          "rules": {
            ".read": true,
            ".write": true,
            "users": {
              ".indexOn": ["id"]
            },
            "userExtras": {
              ".indexOn": ["mac"]
            }
          }
        }
        ```
        
    2. Create empty data
    
        ```
        - root
            - users: false
            - userExtras: false
            - heartbeats: false
            - frames: false
            - messages: false
            - playbacks: false
            - activities: false
            - events: false
            - settings: false
        ```


## DB Structure

```
- root
    - users (many users)
        - USER_ID_1
            - name
            - email
            - avatarUrl
            - pushToken
        - USER_ID_2
        - ...
    - userExtras (one user to many user_extras)
        - USER_ID
            - mac
    - heartbeats (one user to one heartbeat)
        - USER_ID
            - timestamp: (in milliseconds)
    - frames (one user to one frame)
        - USER_ID
            - frame: (base64)
    - messages (one user to many messages)
        - USER_ID
            - MESSAGE_ID_1 (format: yyyyMMddHHmmss-UUID4, e.g. 20191515150030-cb19e916-0cb0-45e0-ae9c-c80ad10484e7)
                - createdAt: (in milliseconds)
                - type: (`alarm` or `remind`)
                - priority: (`standard` or `emergent`)
                - content
            - MESSAGE_ID_2
            - ...
    - playbacks (one message to many playbacks)
        - MESSAGE_ID
            - PLAYBACK_ID_1
                - 0: (base64) 
                - 1
                - 2
                - ...
            - PLAYBACK_ID_2
    - events (one user to many events)
        - USER_ID
            - EVENT_ID_1 (format: TIMESTAMP_IN_MILLISECONDS-UUID4, e.g. 1571158337779-cb19e916-0cb0-45e0-ae9c-c80ad10484e7)
                - code: (e.g. `falldown`, special codes are `create` and `custom`)   
                - name: (for display)
                - type: (`alarm` or `remind`)
                - icon: (see `Name` in `Icon Table`)
                - mode: (`elapsed`, `deadline` or `schedule`)
                - value: (format `mm:ss` for `elapsed` mode, format `HH:mm` for `deadline` mode, format `[HH:mm, HH:mm, ...]` for `schedule`)
                - enabled: (boolean)
            - EVENT_ID_2
    - settings
        - USER_ID
            - push: (boolean)
    - activities
        - USER_ID
            - ACTIVITY_ID_1 (format: TIMESTAMP_IN_MILLISECONDS-UUID4, e.g. 1571158337779-cb19e916-0cb0-45e0-ae9c-c80ad10484e7)
                - date: (string, e.g. 2019-12-12)
                - start_time: (string, e.g. 17:05:59)
                - end_time: (string, e.g. 17:05:59)
                - region
            - ACTIVITY_ID_2
```

## Icon Table

| Name     | ResID                                                     |
|:--------:|:---------------------------------------------------------:|
| outdoor  | event_icon_outdoor_active / event_icon_outdoor_inactive   |
| falldown | event_icon_falldown_active / event_icon_falldown_inactive |
| toilet   | event_icon_toilet_active / event_icon_toilet_inactive     |
| room     | event_icon_room_active / event_icon_room_inactive         |
| exercise | event_icon_exercise_active / event_icon_exercise_inactive |
| goout    | event_icon_goout_active / event_icon_goout_inactive       |
| medicine | event_icon_medicine_active / event_icon_medicine_inactive |
| color_N  | event_icon_color_n / event_icon_color_0                   |

> Note that these resources can be removed by `Remove Unused Resources`

> color N in range [1, 10]
