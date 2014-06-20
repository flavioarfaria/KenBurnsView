KenBurnsView
============

Android library that provides an extension to *ImageView* that creates an immersive
experience by animating its drawable using the [Ken Burns Effect][1].

![Example Image][2]

Overview
--------

**KenBurnsView** provides the following advantages:

* **Control**: you can change the duration and the interpolator of transitions and
pause/resume them. You can also listen to events like *onTransitionStart()* and
*onTransitionEnd()*;
* **Highly extensible**: you can define how the rectangles to be zoomed
and panned will be generated;
* **Libs friendly**: since **KenBurnsView** is a direct extension of *ImageView*,
it seamlessly works out of the box with your favorite image loader library;
* **Easy to use**: you can start using it right away. All you need to do
is to drop the JAR file into your project and replace *ImageView* elements
 in your XML layout files by *com.flaviofaria.kenburnsview.KenBurnsView* ones.

Changelog
---------

**Latest version: 1.0.3**

* [Library JAR][3]
* [Sources JAR][4]
* [Javadoc JAR][5]
* [Sample APK][6]

Basic usage
-----------

*For a working implementation, see the `sample/` folder. You can also download the [Sample APK][6].*

The simplest way to use **KenBurnsView** is by dropping the library JAR file into your project
adding a view to an XML layout file:

    <com.flaviofaria.kenburnsview.KenBurnsView
        android:id="@+id/image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/your_drawable" />

Advanced usage
--------------

1. You can set a listener to your view:

        KenBurnsView kbv = (KenBurnsView) findViewById(R.id.image);
        kbv.setTransitionListener(new TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }
            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });

2. You can pause the animation by calling `kbv.pause()` and resume it
by calling `kbv.resume()`;

3. You can change the duration and the interpolator of transitions:

        RandomTransitionGenerator generator = new RandomTransitionGenerator(duration, interpolator);
        kbv.setTransitionGenerator(generator);

4. If you're willing to have even more control over transitions,
you can implement your own `TransitionGenerator`.

Contribution
------------

Want to contribute? Please, feel free to create a pull request! ;)

License
-------

[Apache Version 2.0][7]

[1]: http://en.wikipedia.org/wiki/Ken_Burns_effect
[2]: http://www.pictureshack.us/images/15526_KenBurnsView.gif
[3]: https://github.com/flavioarfaria/KenBurnsView/releases/download/v1.0.3/KenBurnsView-1.0.3.jar
[4]: https://github.com/flavioarfaria/KenBurnsView/releases/download/v1.0.3/KenBurnsView-1.0.3-sources.jar
[5]: https://github.com/flavioarfaria/KenBurnsView/releases/download/v1.0.3/KenBurnsView-1.0.3-javadoc.jar
[6]: https://sites.google.com/site/flavioarfaria/KenBurnsView-1.0.3-sample.apk
[7]: http://www.apache.org/licenses/LICENSE-2.0.html