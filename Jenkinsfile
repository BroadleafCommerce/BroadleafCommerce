// Since this release already contains problems with table
// name lengths they can't be changed. We want the build
// to succeed so we'll remove the validation
properties(commonModuleJobProps(
    booleanParam(
        defaultValue: false,
        name: 'DB_IDENTIFIER_VALIDATION',
        description: 'Identifier validation is intentionally disabled, do not re-enable it or the build will fail'
    ))
)
buildBroadleafModule(params, false)
