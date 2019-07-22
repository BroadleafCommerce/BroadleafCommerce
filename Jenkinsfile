properties(commonModuleJobProps(
    booleanParam(
        defaultValue: true,
        name: 'FORCE_DEPLOY',
        description: 'By default, non-release builds only auto-deploy to Nexus from branches named develop-x.y.'
                     + ' This executes a deploy regardless of what the branch is with whatever version is currently in the pom.'
    ))
)
buildBroadleafModule(params)
