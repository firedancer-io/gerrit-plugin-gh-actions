alias(
    name = "plugin",
    actual = "gerrit-plugin-gh-actions",
)

java_binary(
    name = "gerrit-plugin-gh-actions",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@gerrit_extension_api//jar:neverlink",
    ],
    deploy_manifest_lines = [
        "Implementation-Title: Gerrit extension for GitHub Actions",
        "Implementation-Version: 0.1",
        "Gerrit-Apitype: extension",
        "Gerrit-PluginName: github-actions",
        "Gerrit-ReloadMode: restart",
        "Gerrit-Module: io.firedancer.contrib.gerrit.gha.Module",
    ],
    main_class = "Dummy",
    visibility = ["//visibility:public"],
    create_executable = False,
)
