Slideshow
=========

Bukkit plugin to create and run slideshows of your world.  Cycle through a list of teleport locations with
scenic views of your world.  The player using this must be able to fly (since most viewing angles are in the sky).

Usage
=====

* `slides new` - start a new slideshow
* `slides add` - add the current location (including viewing direction) to the new slideshow
* `slides save <name>` - save the new slideshow as <name>
* `slides list` - list all available slideshows
* `slides <name>` - run the slideshow <name>

Config
======

slideshows are saved into `plugins/Slideshow/config.yml` directory.  Example config file which has
4 slides in the slideshow named 'test':

    slides:
      test:
        locations:
        - world,-273.72491573955375,85.4132691836773,290.31116882828906,35.250015,151.19955
        - world,-299.21646853355395,77.53826918367729,247.94022984617965,31.50002,236.84967
        - world,-293.12986180853045,97.7882691836773,251.9524968037722,90.0,277.04968
        - world,-260.8855595022034,81.66326918377457,258.28061420357943,56.999996,234.89966

Each location is of the form:

    - worldname,xcoord,ycoord,zcoord,yaw,pitch