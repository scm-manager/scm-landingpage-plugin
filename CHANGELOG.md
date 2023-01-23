# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.12.0 - 2023-01-23
### Added
- Configuration of repository page size

## 1.11.0 - 2022-06-29
### Added
- handling for deleted repositories ([#65](https://github.com/scm-manager/scm-landingpage-plugin/pull/65))

## 1.10.0 - 2022-04-29
### Added
- Enable rendering of content before list inside data extension ([#61](https://github.com/scm-manager/scm-landingpage-plugin/pull/61))

## 1.9.3 - 2022-02-18
### Fixed
- Improve data fetching error display ([#58](https://github.com/scm-manager/scm-landingpage-plugin/pull/58))
- Redirect to landing page after changing the display settings ([#58](https://github.com/scm-manager/scm-landingpage-plugin/pull/58))

## 1.9.2 - 2022-01-07
### Fixed
- High contrast mode findings ([#49](https://github.com/scm-manager/scm-landingpage-plugin/pull/49))

## 1.9.1 - 2022-01-04
### Fixed
- Link in plugin update task

## 1.9.0 - 2021-12-22
### Added
- Widget displaying random feature tip ([#46](https://github.com/scm-manager/scm-landingpage-plugin/pull/46))

## 1.8.0 - 2021-11-16
### Changed
- Change of displayed content has to be submitted explicitly ([#42](https://github.com/scm-manager/scm-landingpage-plugin/pull/42))

## 1.7.0 - 2021-10-21
### Added
- Widgets can now be hidden through a new configuration page ([#40](https://github.com/scm-manager/scm-landingpage-plugin/pull/40))

### Changed
- Integrate plugin content into repository overview page ([#40](https://github.com/scm-manager/scm-landingpage-plugin/pull/40))

## 1.6.0 - 2021-07-30
### Changed
- Fix favorite repository layout to match new repository entry ([#35](https://github.com/scm-manager/scm-landingpage-plugin/pull/35))
- Use react-query instead plain api calls ([#34](https://github.com/scm-manager/scm-landingpage-plugin/pull/34))

## 1.5.0 - 2021-04-22
### Added
- Events for health checks ([#26](https://github.com/scm-manager/scm-landingpage-plugin/pull/26))

## 1.4.1 - 2021-03-05
### Fixed
- Multiple loads ([#23](https://github.com/scm-manager/scm-landingpage-plugin/pull/23))

## 1.4.0 - 2021-03-01
### Added
- Link to import log for repository import events ([#22](https://github.com/scm-manager/scm-landingpage-plugin/pull/22))

## 1.3.1 - 2021-01-29
### Fixed
- Fix "change committed" link in the latest activities ([#21](https://github.com/scm-manager/scm-landingpage-plugin/pull/21))

## 1.3.0 - 2020-12-04
### Added
- Add repository import event for successful and failed imports ([#19](https://github.com/scm-manager/scm-landingpage-plugin/pull/19))

## 1.2.0 - 2020-08-13
### Changed
- Show landingpage for anonymous user ([#18](https://github.com/scm-manager/scm-landingpage-plugin/pull/18))

## 1.1.0 - 2020-07-03
### Added
- Add repository renamed event [#14](https://github.com/scm-manager/scm-landingpage-plugin/pull/14)

### Fixed
- Add permission check before accessing repository data ([#15](https://github.com/scm-manager/scm-landingpage-plugin/pull/15))

## 1.0.0 - 2020-06-04
### Fixed
- Fix error handling in frontend on personal landingpage ([#13](https://github.com/scm-manager/scm-landingpage-plugin/pull/13))
- Rebuild for api changes from core

## 1.0.0-rc2 - 2020-05-26
### Added
- Tooltip for favoring repositories ([#10](https://github.com/scm-manager/scm-landingpage-plugin/pull/10))

### Fixed
- Change plugin permission "manage" to "write" ([#11](https://github.com/scm-manager/scm-landingpage-plugin/pull/11))
- Deleted repositories are now also removed from all users' favourites. ([#12](https://github.com/scm-manager/scm-landingpage-plugin/pull/12))

## 1.0.0-rc1 - 2020-04-14
