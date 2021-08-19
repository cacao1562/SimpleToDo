# SimpleToDo

<p>
  <a href="https://android-arsenal.com/api?level=23"><img alt="API" src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/cacao1562"><img alt="Profile" src="https://img.shields.io/badge/Profile-cacao1562-lightgrey.svg"/></a>
</p>

<br></br>

> 일주일의 할 일를 기록하고 알림 및 알림바 설정, 히스토리를 볼 수 있다.

<br></br>

## Previews
![image](./previews/todo1.png)
![image](./previews/todo2.png)
![image](./previews/todo3.png)
![image](./previews/todo4.png)
![image](./previews/todo5.png)

<br></br>

## Architecture
![image](./previews/final-architecture.png)

<br></br>

## Tech stack & Open-source libraries
- Minimum SDK level 23
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [Dagger2](https://dagger.dev/dev-guide/) for dependency injection.
- JetPack
  - Lifecycle - dispose of observing data when lifecycle state changes.
  - ViewModel - UI related data holder, lifecycle aware.
  - Room - local database.
  - Navigation - helps you implement navigation.

- Libraries
  - [Lottie](https://airbnb.io/lottie/#/) - render animation.
  - [licenses](https://developers.google.com/android/guides/opensource) - include open source notices

