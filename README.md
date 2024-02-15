# JResult
## Description
**JResult** is a Java library designed to handle operation outcomes, making error handling and result processing cleaner and more intuitive. 

By encapsulating the results of operations in a Result<T> object, **JResult** offers a robust alternative to traditional exception handling, enhancing code readability, maintainability, and error management in Java applications.

## Alternative to Exceptions
Using the operation result pattern instead of relying solely on exceptions for error handling in software development offers numerous benefits, contributing to more robust, readable, and maintainable code. 

The operation result pattern makes the control flow more predictable by explicitly returning success or failure outcomes, as opposed to exceptions which can be thrown at any point in the execution flow.

It clearly distinguishes between expected operational outcomes (both success and failure) and exceptional, unforeseen errors, making the code more understandable.

Exception handling can be expensive in terms of performance, especially if exceptions are used frequently as part of normal control flow. The operation result pattern avoids this overhead, potentially leading to performance improvements in error-heavy scenarios.

## Key Features
* **Generic result container**: Handle any operation outcome, success or failure, with a single, type-safe Result<T> class.
* **Fluent API**: Intuitive and fluent interface, making your code more readable and expressive.
* **Enhanced error handling**: Clearly distinguish between expected operation outcomes and exceptional cases, reducing the misuse of exceptions.
* **Composable results**: Easily chain and compose multiple operation results without nested conditionals.
* **Extensible design**: Designed with extensibility in mind, allowing for seamless integration with existing projects and further customization.
## Getting Started
### Include a dependency using your favorite dependency manager
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

#### Gradle (Kotlin)
```Kotlin
implementation("com.panfutov:jresult:0.0.1")
```

### Constructing the result objects
Creating a Result object is a straightforward way to wrap the outcome of operations, which might succeed or fail based on certain conditions. 

There are several static factory methods in the `Result` class which can be used for an operation result creation.

The source code that is used in the examples uses an imaginary method `createAccount(String name)` which returns a `Result` object.
The following example shows a simple way to create result object. The `createAccount(...)` method performs a validation before it creates an account.
```Java
 public Result<Long> createAccount(String name) {
    if (accountExists(name)) {
       return Result.failure("The account is already exists");
    }
    return Result.success(create(name));
 }
```
* **Failure case**: If the account already exists,` Result.failure("The account already exists")` is returned, indicating the operation's failure with an appropriate error message.
* **Success case**: For a new account, the method proceeds with creation and returns `Result.success(create(name))`, encapsulating the new account's identifier.


The `Result` class offers methods for handling the result in functional style using the lambda expressions.

The client code for the `createAccount(...)` method might look like this. It utilizes `ifFailure(...)` and `ifSuccess(...)` methods to execute code depending on the outcome.
```Java
    public void createAccountExample() {
        createAccount("My new account")
                .ifFailure(failure -> System.out.println("Error: " + failure.firstError()))
                .ifSuccess(success -> System.out.println("Created account " + success.getObject()));
    }
```
Or you can use a map a `resolve(...)` function to which you can pass two resolve lambda expressions — for the success and the failure outcomes.
```Java
public void createAccountExample() {
    var account = createAccount("New account")
            .resolve(Result::getNonNullObject, result -> fetchDefault());
}
```

Alternatively, the result can be handled using the traditional `if-else` statements and the boolean flag that indicate the operation success.
```Java
    public void createAccountExample() {
        Result<Long> result = createAccount("My new account");
        if (result.isFailure()) {
            System.out.println("Error: " + result.firstError());
        } else {
            System.out.println("The account has been created " + result.getObject());
        }
    }
```

### Chaining the results
Result chaining is a powerful technique to execute a series of operations where each subsequent operation depends on the success of its predecessor.

#### Sequential execution:
* **Starting point**: The chain initiates with the `createAccount("Account 1")` operation. The continuation of the chain hinges on the success of this initial call.
* **Continuation criteria**: Upon a successful outcome, the next `createAccount(...)` operation is triggered. This pattern repeats, with each operation's success cueing the next in the sequence.
* **Termination on failure**: Should any `createAccount(...)` operation fail, the chain ceases to proceed, preventing any further operations from executing. This ensures the chain's integrity by not executing subsequent operations based on a failed precursor.
```Java
    public void createAccountsExample() {
        createAccount("Account 1")
                .ifSuccess(() -> createAccount("Account 2"))
                .ifSuccess(() -> createAccount("Account 3"))
                .ifSuccess(() -> createAccount("Account 4"));
    }
```

#### Handling failures
The `ifFailure(...)` method can be used to handle a failed result.

**Important!** There can be only **ONE** `ifFailure(...)` handler in a such chain. 

The failure handler is executed only if one of the results failed.
```Java
    public void createAccountsExample() {
        createAccount("Account 1")
                .ifSuccess(() -> createAccount("Account 2"))
                .ifSuccess(() -> createAccount("Account 3"))
                .ifSuccess(() -> createAccount("Account 4"))
                .ifFailure(failure -> System.out.println("Error " + failure.firstError()));
    }

```
Result chaining is an effective pattern for managing sequences of dependent operations, ensuring that each step is contingent upon the success of the previous one and providing a clear mechanism for handling any failures that occur.

### Accessing the results
The `Result` class encapsulates the outcome of operations, parameterized by the type of object it may return upon success. 

Only successful operations populate the result object, aligning the outcome with the operation's intent.

#### Successful operation outcome
For instance, `Result<Long> createAccount()` signifies a method that, upon a successful operation, returns a Long type result.

For operations not returning any specific value, the `Result` class is parameterized with `Void`, e.g.,` Result<Void> createAccount()`, implying a null result object for successful void operations.

#### Retrieving the result object
There are several methods to get the result object from a result.

* `getObject()`: Retrieves the result object, which could be null. 
```Java
    public void getObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getObject());
    }

```

* `getNonNullObject()`: Ensures a non-null result object, throwing a NullPointerException if it's null. 
```Java
    public void getNonNullObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getNonNullObject());
    }
```

* `getObjectOrElse(T)`: Returns the result object or a default value if it's null. 
```Java
    public void getNonNullObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getObjectOrElse(DEFAULT_ACCOUNT));
}
```

* `getObjectOrElse(Supplier<T> supplier)`: Offers a lazy evaluation option for the default value, using a supplier lambda that executes only if the result object is null. 
```Java
    public void getObjectOrElseExample() {
        var result = createAccount("New account");
        System.out.println(result.getObjectOrElse(() -> fetchDefault()));
    }
```

* `getOptionalObject()`: Allows to wrap an object into an `Optional`.
```Java
    public void getOptionalObjectExample() {
        var result = createAccount("New account");
        result.getOptionalObject()
                .map(this::findById);
    }
```

* `mapObject(Function<T, ? extends U> mapper)`: The method designed to apply a transformation function to the result object of a successful operation, converting it into another type.
 It's commonly used in conjunction with `ifSuccess(...)` method, ensuring the transformation occurs only if the preceding operation was successful.
 Keep in mind, that the `mapObject(...)` function will throw an exception if a result object is null.
```Java
    public void mapObjectExample() {
        var result = createAccount("New account");
        if (result.isSuccess()) {
            System.out.println(result.mapObject(this::findById));
        }
    }
```

#### Best Practices
* Utilize `getObject(`) for cases where a null outcome is acceptable or expected.
* Apply `getNonNullObject()` when the operation's result must not be null, ensuring program integrity.
* Opt for `getObjectOrElse(T defaultValue)` to provide a fallback value, enhancing fault tolerance.
* Leverage `getObjectOrElse(Supplier<T> supplier)` for performance-sensitive contexts where constructing the default value is costly or unnecessary unless required.
* Use `getOptionalObject()` to utilize the `Optional` API.

### Managing the errors
The `Result` class is designed to handle not only the outcome of operations but also associated errors. These errors could represent critical failures or non-critical issues in both successful and unsuccessful operations.

Note, the failed operation result always contains at least one error.

There are several methods to work with errors.

* `hasErrors()`: This method checks if there are any errors present in the result. It's a quick way to ascertain the existence of errors without needing to inspect the actual content.
```Java
    public void hasErrorsExample() {
        var result = createAccount("New account");
        if (result.hasErrors()) {
            // Handle the presence of one or more errors
        }
    }
 ```
* `firstError()`: Retrieves the first error in the collection, which is often the primary or only error needed for evaluation. If there are no errors, this method will throw an exception, making it important to use `hasErrors()` beforehand to avoid exceptions.
```Java
    public void firstErrorExample() {
        createAccount("My new account")
                .ifFailure(failure -> System.out.println("Error: " + failure.firstError()));
    }
```
* `errorCount()`:the total number of errors contained within the result.
```Java
    public void errorCountExample() {
        var result = createAccount("My new account");
        System.out.println("Error count:" + result.errorCount());
    }
```
* `errors()`: Provides access to the complete list of errors. This method is invaluable when all errors need to be processed, displayed, or logged.
```Java
    public void errorCountExample() {
        createAccount("My new account")
                .errors().forEach(System.out::println);
    }
```
In many cases, only the first or primary error is relevant. `firstError()` is optimized for such scenarios, though it should be prefaced with a `hasErrors()` check to prevent the exception.

#### Error additional context 
The `Error` class is designed to encapsulate comprehensive information about errors beyond just a simple message. 

It supports including an associated `Throwable` object and a metadata map, enriching the error details and providing deeper insights into the error context.

```Java
    public void errorWithThrowableAndMetadata() {
        var result = Result.failure(
                new Error("Something went wrong", exception, Map.of("Severity", "Critical")));

        System.out.println("Error message " + result.firstError().getMessage());
        System.out.println("Throwable " + result.firstError().getThrowable());
        System.out.println("Metadata" + result.firstError().getMetadata());
    }
```
## License
**JResult** is open-sourced under the MIT license. Feel free to use it, contribute, and spread the word!
