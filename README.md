# JResult
## Description
**JResult** is a Java library designed to handle operation outcomes, making error handling and result processing cleaner and more intuitive. 
By encapsulating the results of operations in a Result<T> object, **JResult** offers a robust alternative to traditional exception handling, enhancing code readability, maintainability, and error management in Java applications.
## Key Features
* Generic Result Container: Handle any operation outcome, success or failure, with a single, type-safe Result<T> class.
* Fluent API: Intuitive and fluent interface, making your code more readable and expressive.
* Enhanced Error Handling: Clearly distinguish between expected operation outcomes and exceptional cases, reducing the misuse of exceptions.
* Composable Results: Easily chain and compose multiple operation results without nested conditionals.
* Extensible Design: Designed with extensibility in mind, allowing for seamless integration with existing projects and further customization.
## Getting Started
### Creating the Result object
The following example shows a simple way to create result object. The method `createAccount(...)` performs a validation before it creates an account.
If an account is already exists it returns a result object for a failed operation using the static factory methods `Result.failure(...)`
Otherwise, it creates an account and returns a result object for a succeeded operation that contains a `long` identifier value of a created account.

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
            System.out.println("The account has been created" + result.getObject());
        }
    }
```

### Chaining the results
There is an option to execute operations that return a result in a chain. The following example shows how to chain multiple `createAccount(...)` 
calls depending on the last executed result. 

In the following example, there are **four** `createAccount(...)` chained calls.
If the result of a first call is failure then all the next calls of the `createAccount(...)` are **NOT** executed. 
If the result of a first call is success then the second call is executed and the success check on the last result is performed again to determined whether the next call should be executed or not.

```Java
    public void createAccountsExample() {
        createAccount("Account 1")
                .ifSuccess(() -> createAccount("Account 2"))
                .ifSuccess(() -> createAccount("Account 3"))
                .ifSuccess(() -> createAccount("Account 4"));
    }
```

**Important!** There can be only **ONE** `ifFailure(...)` handler for such chains. The failure handler is executed only if one of the results failed.
```Java
    public void createAccountsExample() {
        createAccount("Account 1")
                .ifSuccess(() -> createAccount("Account 2"))
                .ifSuccess(() -> createAccount("Account 3"))
                .ifSuccess(() -> createAccount("Account 4"))
                .ifFailure(failure -> System.out.println("Error " + failure.firstError()));
    }

```

### Getting the operation result object
The `Result` class is parametrized with a type that determines the class of the returned object that an operation produces.
The result object **can be set only to the succeeded results**. 

For example, the following method `Result<Long> createAccount()` returns a result with a `Long` object if the operation result is success. 

The void methods should be parametrized with `Void` parameter, e.g. `Result<Void> createAccount()`. In such case the object of the operation result is null.

There are multiple methods to get the result object from a result.
The first is `getObject()` method. Note, that the returned object is nullable.
```Java
    public void getObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getObject());
    }

```

The second is `getNonNullObject()`. This method throws a `NullPointerException` if an object is null.
```Java
    public void getNonNullObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getNonNullObject());
    }
```

The third is `getObjectOrElse(T)`. This method accepts a default value in case the result object is null.
```Java
    public void getNonNullObjectExample() {
        var result = createAccount("New account");
        System.out.println(result.getObjectOrElse(DEFAULT_ACCOUNT));
}
```

The fourth is `getObjectOrElse(Supplier<T> supplier)`. This method accepts a supplier lambda expression which is executed only if the return object is null.
So it allows a lazy evaluation for a default value.
```Java
    public void getObjectOrElseExample() {
        var result = createAccount("New account");
        System.out.println(result.getObjectOrElse(() -> fetchDefault()));
    }
```

## License
**JResult** is open-sourced under the MIT license. Feel free to use it, contribute, and spread the word!
