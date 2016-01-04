# KeyRingFix5x

Xposed module for [Key Ring](https://play.google.com/store/apps/details?id=com.froogloid.kring.google.zxing.client.android) app to fix Nexus 5x camera orientation

The new Google/LG Nexus 5x has a reverse-landscape mounted rear camera, and using it to add rewards/gift cards and shopping list images was frustrating with the viewfinder and preview images being mirrored and flipped 180 degrees. 

This module will override the camera's display orientation, which fixes the viewfinder, and it will force the JPEG orientation to 180, which should display the preview images correctly.

