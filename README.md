# JResult
## Description
**JResult** is a Java library designed to handle operation outcomes, making error handling and result processing cleaner and more intuitive. 
By encapsulating the results of operations in a Result<T> object, **JResult** offers a robust alternative to traditional exception handling, enhancing code readability, maintainability, and error management in Java applications.
## Key Features
* **Generic result container**: Handle any operation outcome, success or failure, with a single, type-safe Result<T> class.
* **Fluent API**: Intuitive and fluent interface, making your code more readable and expressive.
* **Enhanced Error Handling**: Clearly distinguish between expected operation outcomes and exceptional cases, reducing the misuse of exceptions.
* **Composable results**: Easily chain and compose multiple operation results without nested conditionals.
* **Extensible design**: Designed with extensibility in mind, allowing for seamless integration with existing projects and further customization.
## Getting Started
### Constructing the result objects
Creating a Result object is a straightforward way to encapsulate the outcome of operations, which might succeed or fail based on certain conditions. 
There are several static factory methods in the `Result` class which can be used for an operation result creation.

The following example shows a simple way to create result object. The made up method `createAccount(...)` performs a validation before it creates an account.
* **Failure Case**: If the account already exists,` Result.failure("The account already exists")` is returned, indicating the operation's failure with an appropriate error message.
* **Success Case**: For a new account, the method proceeds with creation and returns `Result.success(create(name))`, encapsulating the new account's identifier.

```Java
    public Result<Long> createAccount(String name) {
        if (accountExists(name)) {
            return Result.failure("The account is already exists");
        }
        return Result.success(create(name));
    }
```

The `Result` class offers methods for handling the result in functional style using the lambda expressions.

The client code for the `createAccount(...)` method might look like this.
```Java
    public void createAccountExample() {
        createAccount("My new account")
                .ifFailure(failure -> System.out.println("Error: " + failure.firstError()))
                .ifSuccess(success -> System.out.println("Created account " + success.getObject()));
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
* Starting Point: The chain initiates with the createAccount("Account 1") operation. The continuation of the chain hinges on the success of this initial call.
* Continuation Criteria: Upon a successful outcome, the next createAccount(...) operation is triggered. This pattern repeats, with each operation's success cueing the next in the sequence.
* Termination on Failure: Should any createAccount(...) operation fail, the chain ceases to proceed, preventing any further operations from executing. This ensures the chain's integrity by not executing subsequent operations based on a failed precursor.
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
**Important!** There can be only **ONE** `ifFailure(...)` handler in a such chain. The failure handler is executed only if one of the results failed.
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

#### Best Practices
* Utilize `getObject(`) for cases where a null outcome is acceptable or expected.
* Apply `getNonNullObject()` when the operation's result must not be null, ensuring program integrity.
* Opt for `getObjectOrElse(T defaultValue)` to provide a fallback value, enhancing fault tolerance.
* Leverage `getObjectOrElse(Supplier<T> supplier)` for performance-sensitive contexts where constructing the default value is costly or unnecessary unless required.
 
## License
**JResult** is open-sourced under the MIT license. Feel free to use it, contribute, and spread the word!
