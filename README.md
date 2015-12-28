KenBurnsView
============

Android library that provides an extension to *ImageView* that creates an immersive
experience by animating its drawable using the [Ken Burns Effect][KenBurnsEffect].

![Example Image][SampleImage]

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

[Changelog][Changelog]
---------

**Latest version: 1.0.6**

* [Download JAR from Maven Central][Maven]

Gradle integration
------------------

If you're using Gradle, you can declare this library as a dependency:

	dependencies {
        compile 'com.flaviofaria:kenburnsview:1.0.6'
	}

Basic usage
-----------

*For a working implementation, see the `sample/` folder. You can also download the [Sample APK][SampleApk].*

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

Questions
---------

You can ask any question on Stack Overflow using the [android-kenburnsview][StackOverflowTag] tag.

Used by
-------

If you want your app listed here, drop me a message on [Google+][Gplus].

Icon                                                                                                         | App link
-------------------------------------------------------------------------------------------------------------|-----------
<img src="https://lh6.ggpht.com/Lp1vxT8QfbbJ_-UNSU9_AUpCzg3ekf1hfiKs9zjadSKM3Jlbk2eUPps41GVlzWV-Xzw=w48" />   | [FotMob][FotMob]
<img src="https://lh3.ggpht.com/txlZ48RgPe8afIA39J-IqzSZqsbt2Dz3sht7YAqKbVTIoEi6e5KGG0s1NlIgwz8fQj-n=w48" /> | [Getaways by Groupon][Getaways]
<img src="https://lh3.googleusercontent.com/oGf7pFtEvi6OwLRiR75EI28bY3_AgUfS0Ci7DzuwjCla4jn5d_EIzsESRF5zJnUDXYqX=w48-rw" /> | [just food][JustFood]

Contribution
------------

Want to contribute? Please, feel free to create a pull request! ;)

License
-------

[Apache Version 2.0][License]

[KenBurnsEffect]:   http://en.wikipedia.org/wiki/Ken_Burns_effect
[SampleImage]:      http://www.pictureshack.us/images/15526_KenBurnsView.gif
[Changelog]:        https://github.com/flavioarfaria/KenBurnsView/wiki/Changelog
[Maven]:            http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.flaviofaria%22%20AND%20a%3A%22kenburnsview%22
[SampleApk]:        https://sites.google.com/site/flavioarfaria/KenBurnsView-1.0.6-sample.apk
[StackOverflowTag]: http://stackoverflow.com/questions/tagged/android-kenburnsview
[License]:          http://www.apache.org/licenses/LICENSE-2.0.html
[GPlus]:            https://plus.google.com/+Fl%C3%A1vioFaria1
[FotMob]:           https://play.google.com/store/apps/details?id=com.mobilefootie.wc2010
[Getaways]:         https://play.google.com/store/apps/details?id=com.groupon.travel
[JustFood]:	    https://play.google.com/store/apps/details?id=scientist.jobless.foodmana
