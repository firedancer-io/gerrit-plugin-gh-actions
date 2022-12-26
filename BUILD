alias(
    name = "plugin",
    actual = "github-actions_deploy.jar",
)

java_binary(
    name = "github-actions",
    srcs = glob(["src/main/java/**/*.java"]),
    create_executable = False,
    deploy_manifest_lines = [
        "Implementation-Title: Gerrit extension for GitHub Actions",
        "Implementation-Version: 0.1",
        "Gerrit-Apitype: extension",
        "Gerrit-PluginName: github-actions",
        "Gerrit-ReloadMode: restart",
        "Gerrit-Module: io.firedancer.contrib.gerrit.plugins.gha.Module",
        "Gerrit-HttpModule: io.firedancer.contrib.gerrit.plugins.gha.HttpModule",
    ],
    main_class = "Dummy",
    visibility = ["//visibility:public"],
    deps = [
        "@gerrit_plugin_api//jar:neverlink",
        "@maven//:javax_servlet_javax_servlet_api",
    ],
)
