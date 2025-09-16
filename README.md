# OSMTracker for Android™

![Build](https://github.com/labexp/osmtracker-android/actions/workflows/android.yml/badge.svg?branch=develop)
[![Coverage Status](https://coveralls.io/repos/github/labexp/osmtracker-android/badge.svg?branch=develop)](https://coveralls.io/github/labexp/osmtracker-android?branch=develop)

**OSMTracker for Android™** is a mobile app designed for OpenStreetMap mappers and outdoor adventurers. It lets you log a GPS track to document your journey. Its customizable buttons let you simply add POIs as track points directly inside your GPX track.

It also supports voice recording, picture taking, and note-taking. This is the perfect app to survey a place or a path whether you are hiking, cycling, or exploring new areas.

![Main screen](https://wiki.openstreetmap.org/w/images/thumb/7/7b/OSMTracker-Android-main-screen-en.jpg/200px-OSMTracker-Android-main-screen-en.jpg)

Here is a screenshot of the main screen with its default buttons. You can [customize](https://github.com/labexp/osmtracker-android/wiki/Custom-buttons-layouts) these buttons to your liking.

## Get the App 📲

[<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="80"/>](https://play.google.com/store/apps/details?id=net.osmtracker)
[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/app/net.osmtracker)

## More Info ℹ️

- Find more information in the [documentation](https://github.com/labexp/osmtracker-android/wiki)
- Submit bug reports in the [issue tracker](https://github.com/labexp/osmtracker-android/issues)
- Contributions are welcome, please visit our [contributor guide](https://github.com/labexp/osmtracker-android/blob/master/CONTRIBUTING.md)
- Translations can be done on [Transifex](https://explore.transifex.com/labexp/osmtracker-android/)

## Community & Support 🌐

Join our **Telegram group** to connect with users, developers, translators, and contributors:  
👉 [https://t.me/OSMTracker](https://t.me/OSMTracker)

Use this space for real-time discussions, guidance, and support.  
For bug reports or feature requests, continue using the [GitHub Issues](https://github.com/labexp/osmtracker-android/issues) tracker.


## Note 📝

OSMTracker for Android™ official source code repository is [https://github.com/labexp/osmtracker-android](https://github.com/labexp/osmtracker-android).

## PFandroid dashboard realtime testing ⚡️

The PFandroid dashboard now supports realtime GPS anomaly updates via Socket.IO.

To exercise the integration locally:

1. Start the demo backend:
   ```bash
   cd server
   python -m venv .venv
   source .venv/bin/activate
   pip install -r requirements.txt
   python server.py
   ```
2. Build and install the Android app in **debug** mode. The debug build automatically points to `http://10.0.2.2:5000`.
3. Trigger an event from another terminal:
   ```bash
   curl -s http://localhost:5000/pixel/teste.png > /dev/null
   ```

Every call emits a `novo_evento` payload, incrementing the anomaly counter in `GpsAntifragilFragment` almost instantly. For production deployments, update the `socket_url` resource in `app/build.gradle` to the appropriate `wss://` endpoint.
