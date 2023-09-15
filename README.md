# Cinco Cloud

<div align="center">
    <img src="docs/vuepress/src/.vuepress/public/assets/cinco_cloud_logo.png" width="33%" alt="Cinco Cloud Logo" />
    <div>Cinco Cloud is a development environment for graphical domain-specific modeling tools.</div>
    <hr />
</div>

- [Website](#website)
- [Releases](#releases)
- [Scope](#scope)
- [Roadmap](#roadmap)
- [Project contents](#project-contents)
- [Getting started](#getting-started)
- [Contributing](#contributing)
- [Feedback](#feedback)
- [Documentation](#documentation)
- [License](#license)
- [Related projects and used technologies](#related-projects-and-used-technologies)

## Website

Visit the [Cinco Cloud website](https://scce.gitlab.io/cinco-cloud/) for in-depth information.
And visit the [Cinco website](https://cinco.scce.info/) for a deeper look at what Cinco was made for.

## Releases

There are currently no releases available for Cinco Cloud. If you want to use or test it, you should go through the [manual installation process](#getting-started).

## Scope

- Modernize the Cinco workflow
- Run Cinco in a cloud-based system to eliminate hardware bottlenecks
- Make Cinco more accessible and extensible to developers

## Roadmap

You can find the general roadmap in the form of discussions in our [issues](https://gitlab.com/scce/cinco-cloud/-/issues/?label_name%5B%5D=Discussion).

## Project Contents

This monorepo hosts two major projects: the [Cinco Cloud browser-based frontend and backend](https://gitlab.com/scce/cinco-cloud/-/tree/main/cinco-cloud?ref_type=heads), and the [Cinco Editor](https://gitlab.com/scce/cinco-cloud/-/tree/main/cinco-cloud-archetype?ref_type=heads).
Both can run independently, while the former provides the complete framework to run Cinco Cloud on a server, and the latter provides the actual editor in which Cinco products are built.

## Getting Started

[Set up Cinco Cloud to run locally on your machine](https://scce.gitlab.io/cinco-cloud/content/developer-guide/installation/#preparations)

[Become familiar with the project architecture](https://scce.gitlab.io/cinco-cloud/content/developer-guide/overview/#directories)

[Learn how to build Cinco products in Cinco Cloud](https://scce.gitlab.io/cinco-cloud/content/user-guide/building-cinco-products/)

## Contributing

If you would like to contribute, please contact one of the project owners.
Contributing works by following simple steps:

1. Either create new issues or take ownership of open issues.
2. Implement your solution in a separate feature branch.
3. Create a merge/pull request.

(If you want to get even more involved, you can join the weekly catch-up meeting; contact the project owners for more details).

## Feedback

If you have general feedback or bug reports, you can submit an [issue](https://gitlab.com/scce/cinco-cloud/-/issues/new).

## Documentation

Detailed documentation is under construction and will be available as part of our [wiki website](https://scce.gitlab.io/cinco-cloud/).

## Related projects and Used Technologies

[Theia][theia] - We use Theia as the base for our IDE environments.

[GLSP][glsp] - Our graphical editors use GLSP to provide their modeling languages.

[Sprotty][sprotty] - Used to visualize and edit graphical models.

[Langium][langium] - Textual meta-languages are provided using Langium.

[Helm][helm] - Kubernetes management system.

[Angular/TS][angular] - Angular TypeScript-based frontend.

[Java][java] - Backend language.

[//]: # "Source definitions"
[theia]: https://github.com/eclipse-theia/theia "Theia"
[glsp]: https://github.com/eclipse-glsp/glsp "The Graphical Language Server Platform"
[sprotty]: https://sprotty.org/ "Sprotty"
[langium]: https://langium.org/ "Langium"
[helm]: https://helm.sh/ "Helm"
[angular]: https://angular.io/ "Angular"
[java]: https://www.java.com/de/ "Java"

## License

[EPL2](https://www.eclipse.org/legal/epl-2.0/)
