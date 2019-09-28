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
            - frames: false
            - messages: false
            - playbacks: false
            - settings: false
            - users: false
        ```
