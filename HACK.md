## Build System

As of 2022-12, there are two common flavors of Gerrit plugins, both using Bazel:
1. In-tree plugins (technically Git submodules) built in the `plugins` subdir of the Gerrit monorepo
2. Out-of-tree plugins built with tools from the `bazlets` repo.

As this plugin covers a niche use-case, it's not going to be merged in-tree anytime soon.
Therefore, we maintain it as an out-of-tree module.

There are flaws with the `bazlets` approach, however:
- The Gerrit extension API is unsupported (Fixed by https://gerrit-review.googlesource.com/c/bazlets/+/355594)
- The `maven_jar` rule is not exported by `bazlets`, so we have to mix Bazel's own Maven build system with `rules_jvm_external`.

## plugins_github

Another Gerrit plugin integrating with GitHub is https://github.com/GerritCodeReview/plugins_github

This plugin also has a webhook server.
Ideally, we would link against this plugin and inject a listener to receive webhook notifications.
This would allow a single webhook endpoint to serve both `github-plugin` and `gerrit-plugin-gha`.

Sadly, `github-plugin` is not modular enough.
The webhook servlet contains a static event manager that dispatches notifications to classes matching `com.googlesource.gerrit.plugins.github.notification.*Handler`.
See here: https://github.com/GerritCodeReview/plugins_github/blob/67deb78f3a6fb54ff467ecd9a3f923f9380195de/github-plugin/src/main/java/com/googlesource/gerrit/plugins/github/notification/WebhookServlet.java#L75

We could craft our own class at this import path but this approach might collide at any time in the future.

Therefore, to maintain API stability, this plugin just ships its own, separate webhook endpoint.
