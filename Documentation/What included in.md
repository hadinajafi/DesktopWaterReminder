Using System trayIcon for staying the App open in background and showing notifications periodically.
Libraries: Jackson
  For store user data in JSON files.

TrayIcon package: java.awt.TrayIcon;
System tray: java.awt.SystemTray;
Image for show in tray: java.awt.Image;

first of all we check if the system supports tray or not.
after, we create image, trayIcon, and systemTray objects. system tray can have ToolTip also.
next step is adding trayIcon to systemTray and then displaying message with trayIcon.

** MainWindow **:
1. AnchorPane is the main container for ability of anchoring components on resizing.
2. contains a tabbed layout for "Daily", "Settings" pages right now. (Refere to the future plan file, there will be more like food calories, monthly view and notification manager.)
3. Another anchorPane inside Daily tab to show these information:
  a. next notification and skip button to skip it.
  b. Pie chart to show how mcuh user drunk water today.
4. AnchorPane for showing next notification status and recent one. also can done it or ignore it before it's time.
