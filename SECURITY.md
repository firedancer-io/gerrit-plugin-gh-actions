Please familiarize yourself with the security considerations of this plugin before deploying it.

- Allowing unauthorized users to dispatch CI workflows is a security risk.
  For example, an attacker could attempt to poison the CI cache to induce side effects.

  We therefore recommend:
    - Configure the "Allow-CI" label to be _not satisfied_ by default.
      This will force an authorized user to selectively approve CI runs
    - Sparingly use _override conditions_ to exempt trusted users from this rule.

- Ensure that the "Allow-CI" and "CI-Result" labels are reset if a new revision is pushed to a review (`copyCondition: false`).

- This plugin exposes a public HTTP API on your Gerrit server for GitHub webhooks.
    - Follow general web hosting best practices (e.g. deploy a web application firewall).
    - Since anyone could call this API, a _secret token_ is used to authenticate that the source of requests is indeed GitHub.
      For more info, refer to [_GitHub: Securing your webhooks_](https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks).

Please responsibly disclose any further security concerns at `firedancer-devs [AT] jumptrading [DOT] com`.
