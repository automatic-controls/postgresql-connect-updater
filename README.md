# AutoUpdater

WebCTRL is a trademark of Automated Logic Corporation.  Any other trademarks mentioned herein are the property of their respective owners.

This add-on comes pre-packed with [postgresql-connect](https://github.com/automatic-controls/postgresql-connect) and is used as an automatic self-update mechanism. When the primary add-on senses that it should be updated, it downloads the new add-on file from an SFTP server. Then it extracts and installs this update component to the server. When this update component activates, it removes the old PostgreSQL connector add-on and installs the new downloaded version. When the new version starts up, this update component is automatically removed. So this add-on will only be installed on a WebCTRL server for a couple seconds at a time, and users will generally never know about it.