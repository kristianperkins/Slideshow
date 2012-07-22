#Slideshow#

Bukkit plugin to create and run slideshows of your servers worlds. Cycle through a list of teleport
locations with scenic views.

The player using this must be able to fly (since most viewing angles are in the sky).

##Usage##

###Player Commands###

* `/slides add` - add your current location (including viewing direction) to the new slideshow
* `/slides save <name>` - save the new slideshow as <name>
* `/slides <name>` - run the slideshow <name>
* `/slides` - list all available slideshows

###Console Commands###

* `slides` - list all available slideshows

##Permissions##

Must have the `slideshow.basic` permission.  Separate permissions for add/save/run/list coming.

##Config##

slideshows are saved into `plugins/Slideshow/config.yml` directory.  Example config file which has
4 slides in the slideshow named 'scenic':

    slideshows:
      scenic:
        slides:
        - world: world
          x: -216.5599075500681
          y: 83.41326918367727
          z: 298.1087455864209
          yaw: 257.6998
          pitch: 45.750042
        - world: world
          x: -216.5599075500681
          y: 64.0
          z: 298.1087455864209
          yaw: 34.949738
          pitch: 13.350043
        - world: world
          x: -254.47851348303448
          y: 58.38920272000321
          z: 303.40247912097806
          yaw: 64.9498
          pitch: 15.750045
          message: Checkout the view!
        - world: world
          x: -248.8414216395152
          y: 71.0
          z: 251.07486458903276
          yaw: 171.44945
          pitch: -1.6499997

[Mod page on Bukkit Dev](http://dev.bukkit.org/server-mods/slideshow/)