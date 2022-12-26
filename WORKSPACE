workspace(name = "gerrit-plugin-gh-actions")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

# Gerrit Bazlets
# --------------

git_repository(
    name = "com_googlesource_gerrit_bazlets",
    commit = "e68cc7a45d9ee2b100024b9b12533b50a4598585",  # master (as of 2022-12-26)
    remote = "https://gerrit.googlesource.com/bazlets",
)

load("@com_googlesource_gerrit_bazlets//:gerrit_api.bzl", "gerrit_api")

gerrit_api()

# Maven Deps
# ----------

http_archive(
    name = "rules_jvm_external",
    sha256 = "b17d7388feb9bfa7f2fa09031b32707df529f26c91ab9e5d909eb1676badd9a6",
    strip_prefix = "rules_jvm_external-4.5",
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/4.5.zip",
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "javax.servlet:javax.servlet-api:3.1.0",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
