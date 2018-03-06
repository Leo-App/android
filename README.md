# leoapp-sources
SourceCode Repository for the LeoApp 2017

### Version
Die Versionsnummer in app.gradle sollte immer um eins höher sein, als die im Playstore, das gilt sowohl für versionCode als auch für versionName.

### Branches

#### master
Die master Version der LeoApp muss:

+ stable sein
+ *Immer* deploybar sein
+ Nach Möglichkeit vollständig dokumentiert sein

_In master wird nicht gearbeitet._

### release
Im release-Branch werden alle Bugfixes, bzw. alle Issues mit dem Milestone "Releaseversion" gefixt/bearbeitet. Bei Release am 16.3. wird release in master gepullt.

### update
Im update-Branch werden alle Issues mit dem Milestone "Update 1" bearbeitet. Bei dem entsprechenden Update wird update in master gepullt und ein neuer update Branch erstellt.

#### development
Wenn geplante Änderungen nicht minimal sind, bitte im development-Branch arbeiten und vollständig testen, bevor Pull requests erstellt werden. Außerdem sollte ggf. die Versionsnummer geändert werden.
