# Field Masking Library

# Overview

__This library provides a simple and flexible way to mask sensitive data in logs. It allows masking entire strings or specific parts of them, making it suitable for any Java project that needs to protect confidential information in logs.__

## **Features**

* Full masking of strings

* Partial masking with configurable ranges

* Customizable masking characters

* Designed to be easily integrated into any Java project

# Installation

Add the following dependency to your pom.xml if using Maven:
```xml
<dependency>
    <groupId>ru.belovia.masklib</groupId>
    <artifactId>field-masking</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or if using Gradle:

```gradle
dependencies {
    implementation 'ru.belovia.masklib:1.0.0'
}
```