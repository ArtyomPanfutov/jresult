# Releases
## 0.0.2
### Description
* Add method `Result<T> ifSuccessApply(Function<Result<T>, Result<T>> function)`
* Add method `Result<T> ifSuccessApplyToObj(Function<T, Result<T>> function)`
* Add method `void forEachError(Consumer<? super GenericError> action)`
#### Apache Maven
```xml
<dependency>
    <groupId>com.panfutov</groupId>
    <artifactId>jresult</artifactId>
    <version>0.0.2</version>
</dependency>
```

#### Gradle
```dockerfile
implementation group: 'com.panfutov', name: 'jresult', version: '0.0.2'
```
## 0.0.1
### Description
Initial release
#### Apache Maven
```xml
<dependency>
    <groupId>com.panfutov</groupId>
    <artifactId>jresult</artifactId>
    <version>0.0.1</version>
</dependency>
```

#### Gradle
```dockerfile
implementation group: 'com.panfutov', name: 'jresult', version: '0.0.1'
```
