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
              ".indexOn": ["id", "rpi-mac"]
            }
          }
        }
        ```
        
    2. Create empty data
    
        ```
        - root
            - users: false
            - heartbeats: false
            - frames: false
            - messages: false
            - playbacks: false
            - settings: false
        ```


## DB Structure

```
- root
    - users (many users)
        - USER_ID_1
            - name
            - avatarUrl
            - pushToken
        - USER_ID_2
        - ...
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
    - settings (one user to one setting) 
```
