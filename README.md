<div align="center">
    <img src="./docs/vuepress/src/.vuepress/public/assets/cinco-logo.png" width="100%" alt="Cinco Cloud Logo" />
    Cinco Cloud is a development environment for domain-specific modeling tools. It is the cloud based evolution of [Cinco](https://cinco.scce.info/about/).
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

Visit the [Cinco Cloud Website](https://scce.gitlab.io/cinco-cloud/) for indepth information.
And visit the [Cinco Website](https://cinco.scce.info/) for an indepth look at what Cinco was made for.

## Releases

There are currently no releases for Cinco Cloud. If you want to use or test it, you will have to go through the [manual installtion process](#getting-started).

## Scope

- Modernize the Cinco workflow
- Run Cinco in a cloud based system, to eliminate hardware bottlenecks
- Make Cinco more accessible and extendable for developers

## Roadmap

You can find the general roadmap in form of discussions in our [issues](https://gitlab.com/scce/cinco-cloud/-/issues/?label_name%5B%5D=Discussion).

## Project Contents

This monorepo hosts two major projects: the [Cinco Cloud browser based frontend and backend](https://gitlab.com/scce/cinco-cloud/-/tree/main/cinco-cloud?ref_type=heads) and the [Cinco Editor](https://gitlab.com/scce/cinco-cloud/-/tree/main/cinco-cloud-archetype?ref_type=heads).
Both can run independently, while the former provides the complete framework to run Cinco Cloud on a server and the latter provides the actual editor in which Cinco products are built.

## Getting Started

[Set up Cinco Cloud to run locally on your machine](https://scce.gitlab.io/cinco-cloud/content/developer-guide/installation/#preparations)

[Get familliar with the project architecture](https://scce.gitlab.io/cinco-cloud/content/developer-guide/overview/#directories)

[Check out how to build Cinco products in Cinco Cloud](https://scce.gitlab.io/cinco-cloud/content/user-guide/building-cinco-products/)

## Contributing

If you want to contribute, please contact any of the project owners.
Contribution works by following simple steps:
- Either create new issues or take responsibility for any open issues.
- Implement your solution on a seperate feature branch.
- Create a merge/pull request.

(Anm. If you want to get even further involved, you can join the weekly stand-up; you can get more details from the project owners.

Anstatt project owners lieber die jeweiligen direkt benennen?)

## Feedback

If you have general feedback or bug reports, you can submit an [issue](https://gitlab.com/scce/cinco-cloud/-/issues/new).


## Documentation

Indepth Documentation is under construction and will be found as part of our [website](https://scce.gitlab.io/cinco-cloud/).

## Related projects and Used Technologies

[Theia][theia] - We are using Theia as a foundation for our editor.

[GLSP][glsp] - Our graphical editor is based on the GLSP project.

[Docker][docker] - Facilitating set up and development.

[Helm][helm] - Kubernetes management system.

[Angular/TS][angular] - Angular TypeScript based frontend.

[Java][java] - Backend language.

[//]: # "Source definitions"
[theia]: https://github.com/eclipse-theia/theia "Theia"
[glsp]: https://github.com/eclipse-glsp/glsp "The Graphical Language Server Platform"
[docker]: https://www.docker.com/ "Docker"
[helm]: https://helm.sh/ "Helm"
[angular]: https://angular.io/ "Angular"
[java]: https://www.java.com/de/ "Java"

## License

[EPL2](https://www.eclipse.org/legal/epl-2.0/)








