package com.example.a18hw.entity

sealed class State {
    object AllPermissionsGranted : State()
    object PermissionsNotGranted : State()
}
