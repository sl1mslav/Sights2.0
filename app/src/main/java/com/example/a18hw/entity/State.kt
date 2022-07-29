package com.example.a18hw.entity

sealed class State {
    object ALL_PERMISSIONS_GRANTED : State()
    object PERMISSIONS_NOT_GRANTED : State()
}
