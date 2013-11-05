### Issues
We use GitHub issues heavily for reporting bugs. See the [issues tab](https://github.com/BroadleafCommerce/BroadleafCommerce/issues) of this project to open a bug.

### Code Contributions
If you see a bug in Broadleaf then the best way to ensure it's resolved is to fix it yourself! We welcome any and all pull requests of code that you would like to see within Broadleaf. While we may not accept all pull requests, we would love for you to kick off a discussion. Key guidelines to getting your pull request accepted:

1. Requests should be based off of the 'develop' branch if you want to target the next Broadleaf release. To target older versions of Broadleaf, use the corresponding branch (version 2.0.x should use 'BroadleafCommerce-2.0.x', version 2.2.x should be based off of the 'BroadleafCommerce-2.2.x' branch, etc).
2. Try to match the Broadleaf code styles as closely as possible
    > Of particular importance is our ordering of imports. The package prefix ordering of class imports that we configure in our IDEs is:

        1. org
        2. com
        3. java
        4. javax

3. Provide comments on non-obvious code functionality
4. Provide a unit test demonstrating your code
5. Ensure that all tests successfully pass with Maven (`mvn test` passes all tests)
