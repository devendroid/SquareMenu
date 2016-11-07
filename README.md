# SquareMenu
[![Download](https://api.bintray.com/packages/ideal/maven/squaremenu/images/download.svg) ](https://bintray.com/ideal/maven/squaremenu/_latestVersion)
[![API](https://img.shields.io/badge/API-9%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=9)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SquareMenu-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/4585)
[![GitHub license](https://img.shields.io/github/license/dcendents/android-maven-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

SquareMenu is a custom Floating Action Button with a different shape from traditional FABs and with three sub menu buttons.
##Demo
![SquareMenu Demo](/assets/square_menu_v1.0.0.gif)

##Dependency
- Add the dependencies to your app level build.gradle file:

####Gradle
```gradle
    compile 'com.devs:squaremenu:1.0.0'
```
####Maven
```xml
<dependency>
<groupId>com.devs</groupId>
<artifactId>squaremenu</artifactId>
<version>1.0.0</version>
<type>pom</type>
</dependency>
```

##Usage
- Inside your xml:
```xml
<com.devs.squaremenu.SquareMenu
        android:id="@+id/square_menu"
        app:fabSize="80"
        app:fabColor="@color/colorAccent"
        app:menuOpenDirection="top_left"
        app:iconM1="@drawable/ic_delete_forever_white_24dp"
        app:iconM2="@drawable/ic_call_white_24dp"
        app:iconM3="@drawable/ic_chat_white_24dp"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```

- Inside your java:
```java
@Override
public void onCreate() {
     super.onCreate();
    ...
    SquareMenu mSquareMenu = (SquareMenu) findViewById(R.id.square_menu);
        mSquareMenu.setOnMenuClickListener(new OnMenuClickListener(){
            @Override
            public void onMenuOpen() {  }
            @Override
            public void onMenuClose() { }
            @Override
            public void onClickMenu1() { }
            @Override
            public void onClickMenu2() { }
            @Override
            public void onClickMenu3() { }
        });
}

```

## Contribute:
#### Simple 3 step to contribute into this repo:
```
    Fork the project.
    Make required changes and commit.
    Generate pull request. Mention all the required description regarding changes you made.
```

## License
```
Copyright 2016 Deven Singh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
