package com.ksidelta.libruch.utils

import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> ResponseEntity<T>.assertOK() {
    assertThat(this.statusCode, CoreMatchers.equalTo(HttpStatus.OK))
}

fun <T> ResponseEntity<T>.assertBadRequest() {
    assertThat(this.statusCode, CoreMatchers.equalTo(HttpStatus.BAD_REQUEST))
}

fun <T> ResponseEntity<T>.assertBodyThat(matcher: Matcher<in T?>) {
    assertThat(this.body, matcher)
}
