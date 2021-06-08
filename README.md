# Static Code Metrics (scm)
## Overview
Simple cli to output static code metrics supporting multiple languages.

## Installation
    gradle :app:build

## Usage
    java --illegal-access=deny -cp "app/build/libs/*" io.blackpine.scm.Main

## TODO
- allow third party analyzer classes (include jar and pass extension:class and parameters)
- implement JavascriptAnalyzer
- implement PythonAnalyzer
- implement ScalaAnalyzer
