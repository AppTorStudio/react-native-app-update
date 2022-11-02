# react-native-app-update
This plugin supports retrieving app update information on Android and iOS.
## Installation

```sh
npm install react-native-app-update
```

## Usage

```js
import { openAppStore, getAppUpdateInfo } from "react-native-app-update";

// ...

const result = await getAppUpdateInfo();
```


#### AppUpdateInfo

| Prop                              | Type                                                                                | Description                                                                                                                                                                    |
| --------------------------------- | ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`currentVersion`**              | <code>string</code>                                                                 | Version code (Android) or CFBundleShortVersionString (iOS) of the currently installed app version. Only available for Android and iOS.                                         |
| **`availableVersion`**            | <code>string</code>                                                                 | Version code (Android) or CFBundleShortVersionString (iOS) of the update. Only available for Android and iOS.                                                                  |
| **`availableVersionReleaseDate`** | <code>string</code>                                                                 | Release date of the update in ISO 8601 (UTC) format. Only available for iOS.                                                                                                   |
| **`updateAvailability`**          | AppUpdateAvailability             | The app update availability. Only available for Android and iOS.                                                                                                               |
| **`updatePriority`**              | <code>number</code>                                                                 | In-app update priority for this update, as defined by the developer in the Google Play Developer API. Only available for Android.                                              |
| **`immediateUpdateAllowed`**      | <code>boolean</code>                                                                | `true` if an immediate update is allowed, otherwise `false`. Only available for Android.                                                                                       |
| **`flexibleUpdateAllowed`**       | <code>boolean</code>                                                                | `true` if a flexible update is allowed, otherwise `false`. Only available for Android.                                                                                         |
| **`clientVersionStalenessDays`**  | <code>number</code>                                                                 | Number of days since the Google Play Store app on the user's device has learnt about an available update if an update is available or in progress. Only available for Android. |
| **`installStatus`**               | FlexibleUpdateInstallStatus | Flexible in-app update install status. Only available for Android.                                                                                                             |
| **`minimumOsVersion`**            | <code>string</code>                                                                 | The minimum version of the operating system required for the app to run in iOS. Only available for iOS.                                                                        |



## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---
