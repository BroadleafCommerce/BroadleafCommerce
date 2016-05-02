# Broadleaf Commerce Information
Broadleaf Commerce is an open source eCommerce framework that leverages best of breed open source components and frameworks including Spring and Hibernate.

This page provides an overview of ways to interact with Broadleaf Commerce but focuses primarily on the process to file bugs for the framework.

- [Overview of Community Resources](#overview-of-community-resources)
- [Creating Issues / Bugs](#issues)
- [Broadleaf Commerce Branching Strategy](#broadleaf-commerce-branching-strategy)

## Note on Premium Support and Licensing
Broadleaf Commerce also has an enterprise edition with additional features and support that are recommended for larger companies.
See our pricing page (http://www.broadleafcommerce.com/pricing) for more details.


## Overview of Community Resources
- [Issue Tracking](https://github.com/BroadleafCommerce/BroadleafCommerce/issues) - GitHub issues are used to track bugs.
- [Forums](http://forum.broadleafcommerce.com) - Forums are useful for requesting help from other community members.
- IRC - Our IRC channel is [#broadleaf on irc.freenode.net](irc://irc.freenode.net/broadleaf)
- [Documentation](http://docs.broadleafcommerce.org) - In addition to JavaDoc'd classes and services, documentation and tutorials are available.
- [Demo](http://www.broadleafcommerce.com/demo) - You can sign up for your own private demo with a site and admin 

Note: The broadleaf framework consists of the core framework as well as other open source properties.  For example, the Broadleaf Demo application is also open source.   Bugs can and should be filed directly against the demo application (or any other related Broadleaf project).

## Issues
We use GitHub issues for bug tracking. See the [issues tab](https://github.com/BroadleafCommerce/BroadleafCommerce/issues) of this project to open a bug.

### When to open issues
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

-- It helps us if you  make sure that you are using the latest patch release for the version of Broadleaf that you are on and if you check to see if anyone else has filed the same issue.

### Our issue management process
Our goal is to classify issues within a few days after they are filed.   We use the following GitHub labels for this purpose.

##### Step 1 - We assign issue meta-data
We use the following GitHub labels to classify issues.
- **severity-(critical/major/minor)** - issue importance. Critical issues are those that have a large impact on functionality or give incorrect data such as errors in pricing or promotions.
- **module-(admin/cms/core/rest/tests)** - which of the Broadleaf framework modules the issue occurs in
- **type-(bug/enhancement/feature)** - enhancements and features are similar but features will usually become pull requests and represents nontrivial changes. Enhancements are small changes by nature.
- **requires-migration** - used for issues that we need to ensure that we have migration docs for. This could be database changes or large API changes that impact backwards compatibility
- **difficulty-(hard/medium/easy)** - how difficult we think an issue will be. This might be a rough estimate
- **affects-(x.x.x-GA)** - which GA version line the issue is affecting
- **needs-information** - more information is needed in order to continue. We might be waiting on a stack trace or for users to verify with the latest patch version, etc.

##### Step 2 - We select issues for next Broadleaf sprint / patch release
Generally, issues that have been properly classified and for which we have enough information will be eligible for our next Sprint Planning session.
During our Sprint Planning, we will select issues based on the classification factors.

##### Step 3 - We assign a milestone
Items will be assigned a milestone indicating the GA release that we are targeting for the issue.

Milestones are tied to GA releases of the framework.  We use special milestones to help us stay organized such as:
- **Reviewed - Not Yet Assigned** - someone on the Broadleaf team has looked at the issue, has confirmed that it is valid and all of the data is available to start work, it just hasn't been prioritized yet
- **Closed - No Release Required** - reserved for things like duplicates or for closing issues that are more suitable for the forums

Once a GA milestone has been selected and it has been assigned to someone then that means that we are committed to putting that into the referenced milestone release. 
Currently we are targeting ~3 week cycles for patch releases. We do our best but cannot guarantee issue fix times AT ALL.   

If you need an issue or feature on a specific timeline, [contact us](http://www.broadleafcommerce.com/contact) for professional services or support OR fix it yourself and send us a pull request (see next section).

##### Step 4 - We mark the issue as closed
We may ask you (as the submitter) to verify an issue before closing but generally once the developer has committed the fix, we mark the issue as closed.   You can pickup this change immedidately after that point by using the SNAPSHOT version of your current release.

##### Step 5 - We release the patch (or new major version) of the software
Once we've completed the issues for a release, we'll create an official release of the code.

## Broadleaf Commerce Branching Strategy
The latest GA release is available on the `master` branch. Once a GA release has occurred, development continues on corresponding version branches of the form:

> BroadleafCommerce-a.b.x

where:
- **a** = Broadleaf *major* version
- **b** = Broadleaf *minor* version
- **x** = Broadleaf *patch* version 

Upgrading to the next "patch" version should be a drop-in of the new code with minimal testing required to accept the patch.
Upgrading to the next "minor" version usually implies some data migration, configuration changes, or schema changes
Upgrading to the next "major" version implies the same as minor and a full-regression test of your implementation is recommended.


The latest SNAPSHOT version is available on the `develop` branch. So given the scenario where we have released Broadleaf 5.0.0-GA from develop:

- A new branch called `BroadleafCommerce-5.0.x` is created targeting **5.0.1-SNAPSHOT** in preparation for 5.0.1-GA
- The `develop` branch targets **5.1.0-SNAPSHOT** in preparation of 5.1.0-GA
- The `master` branch now holds the 5.0.0-GA code

Subsequent patch releases of 5.0 (5.0.1-GA, 5.0.2-GA, etc) will now be made from the `BroadleafCommerce-5.0.x` branch. Until 5.1.0-GA is released, these 5.0 patch releases will also be merged into master.