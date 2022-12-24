workspace(name = "gerrit-plugin-gh-actions")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "com_googlesource_gerrit_bazlets",
    commit = "a27d983a9b1f97fb83f2c07a0534fb6576063944",
    # branch = "refs/changes/94/355594/1",
    remote = "https://gerrit.googlesource.com/bazlets",
)

load("@com_googlesource_gerrit_bazlets//:gerrit_api.bzl", "gerrit_api")

gerrit_api()
