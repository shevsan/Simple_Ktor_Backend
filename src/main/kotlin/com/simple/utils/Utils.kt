package com.simple.utils

import java.util.regex.Pattern

fun String.isPhoneNumberValid(): Boolean =
    Pattern.compile("^\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$").matcher(this).matches()