# Community Resources

- [Bug Tracker](https://github.com/BroadleafCommerce/BroadleafCommerce/issues)
- [Forums](http://forum.broadleafcommerce.com)
- IRC - #broadleaf on irc.freenode.net

# Premium Support and Licensing
See our pricing page (http://www.broadleafcommerce.com/pricing) for more details.

# Issues
We use GitHub issues for bug tracking. See the [issues tab](https://github.com/BroadleafCommerce/BroadleafCommerce/issues) of this project to open a bug.

### When to Open Issues
There are 2 main avenues for community involvement: GitHub issues and [the forums](http://forum.broadleafcommerce.org). There is somewhat of a fine line that differentiates them but in general you can think of it like this:

- Questions like 'how do I do xyz in Broadleaf?' or 'I'm thinking about doing abc, what do you think?' are suitable for the **forums**
- 'Something doesn't work correctly' or 'there is an exception when I do something in the admin' are suitable for **issues**

Sometimes we might direct you to open issues/forum posts depending on the nature of your question.

### How to open issues
All issues should, at a minimum, provide the following:

1. Broadleaf version you are using
2. Steps to reproduce
3. Any stack traces that you receive (if applicable)
4. Any additional information that allows us to help you faster
5. Any potential fixes you might have already tested

### Issue Metadata
#### Labels
- **severity-(critical/major/minor)** - issue importance. Critical issues are those that have a large impact on functionality or give incorrect data such as errors in pricing or promotions.
- **module-(admin/cms/core/rest/tests)** - which of the Broadleaf framework modules the issue occurs in
- **type-(bug/enhancement/feature)** - enhancements and features are similar but features will usually become pull requests and represents nontrivial changes. Enhancements are small changes by nature.
- **requires-migration** - used for issues that we need to ensure that we have migration docs for. This could be database changes or large API changes that impact backwards compatibility
- **difficulty-(hard/medium/easy)** - how difficult we think an issue will be. This might be a rough estimate
- **affects-(x.x.x-GA)** - which GA version line the issue is affecting
- **needs-information** - more information is needed in order to continue. We might be waiting on a stack trace or for users to verify with the latest patch version, etc.

Issues should, at a minimum, have a **severity**, **type**, **module** and **affects** very soon after creation.

#### Milestones
Milestones are tied to GA releases of the framework. Some special milestones that we use are:
- **Reviewed - Not Yet Assigned** - someone on the Broadleaf team has looked at the issue, has confirmed that it is valid and all of the data is available to start work, it just hasn't been prioritized yet
- **Closed - No Release Required** - reserved for things like duplicates or for closing issues that are more suitable for the forums

Once a GA milestone has been selected and it has been assigned to someone then that means that we are committed to putting that into the referenced milestone release. Currently we are targeting ~3 week cycles for patch releases.

## Pull Requests

What's better than issues? Pull requests, of course! The quickest way to get an issue fixed is to fork Broadleaf and submit a patch to the codebase. **Pull requests will receive priority access to getting reviewed and a fix in place.** While we may not accept all pull requests, we would love for you to kick off a discussion around some code. Key guidelines to getting your pull request accepted:

1. Requests should be based off of the 'develop' branch if you want to target the next Broadleaf release. To target older versions of Broadleaf, use the corresponding branch (version 3.0.x should use 'BroadleafCommerce-3.0.x', version 2.2.x should be based off of the 'BroadleafCommerce-2.2.x' branch, etc).
2. Try to match the Broadleaf code styles as closely as possible
    > Of particular importance is our ordering of imports. The package prefix ordering of class imports that we configure in our IDEs is:

        1. org
        2. com
        3. java
        4. javax

3. Provide comments on non-obvious code functionality
4. Ensure that all tests successfully pass with Maven (`mvn test` passes all tests)

### Branching Strategy
The latest GA release is available on the `master` branch. Once a GA release has occurred, development continues on corresponding version branches of the form:

> BroadleafCommerce-a.b.x

where:
- **a** = Broadleaf *major* version
- **b** = Broadleaf *minor* version

The latest SNAPSHOT version is available on the `develop` branch. So given the scenario where we have released Broadleaf 3.0.0-GA from develop:

- A new branch called `BroadleafCommerce-3.0.x` is created targeting **3.0.1-SNAPSHOT** in preparation for 3.0.1-GA
- The `develop` branch targets **3.1.0-SNAPSHOT** in preparation of 3.1.0-GA
- The `master` branch now holds the 3.0.0-GA code

Subsequent patch releases of 3.0 (3.0.1-GA, 3.0.2-GA, etc) will now be made from the `BroadleafCommerce-3.0.x` branch. Until 3.1.0-GA is released, these 3.0 patch releases will also be merged into master.

So, if you want to make changes to include in **3.0.4-GA** then you should use the `BroadleafCommerce-3.0.x` branch which will likely be targeting **3.0.4-SNAPSHOT**.

### Setting up a Pull Request
All of us at Broadlaef rely heavily on [Jrebel](http://zeroturnaround.com/software/jrebel/) to speed up our development. Our normal development cycle is:

1. Clone the framework
2. Check out the correct branch (see above for which branch to use)
3. Execute a `mvn clean install -Pblc-development` on the command line or from Eclipse at the root of the Broadleaf project
> the `blc-development` Maven profile will create the appropriate metadata for JRebel to reload the framework classes
4. Clone a clean version of [DemoSite](https://github.com/BroadleafCommerce/DemoSite) (or use your own)
5. Target the same SNAPSHOT version of the framework in your root DemoSite pom.xml that you just built with Maven in previous steps
6. Submit your code in a pull request

### Style Guides
//TODO - upload Eclipse style config file
