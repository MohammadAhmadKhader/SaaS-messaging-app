##  Overview

This application is similar to Slack/Discord more like discord, messaging application where users can create organizations (like servers in discord and workspaces in slack).

##  User-Facing Features

- User can create organizations.
- User can subscribe to organizations.
- Global Roles based permissions per Users.
- Organization Roles based permissions.
- Users can be kicked and restricted (banned) from orgnaizations, also can be invited to organizations.
- Organizations have Categories where Categories have channels where users can send messages.
- Payment gateway is implemeted using Stripe, users can subscribe to an Organziation.
- Users can add each others as friends.
- Users can search for other users and organizations.
- Users can send messages to each other on a Conversation (privately).
- Users account gets locked on 5 failure attempts for 15 mins.

##  Technical Features

- Authentication system is implemented with Spring security.
 Authorization is role-based, supporting two types of roles:
   - **Organization Roles**: Roles scoped to a specific  organization - (owner, admin, user), allowing specific controls within the organization.
   - **Global Roles**: System-wide roles (owner, admin, user) that apply across all the system (but not within the organizations).
- Rate limting using Token Bucket Algorithm.
- Stripe Checkout Sessions to manage users organizations subsecriptions.
- Handling Stripe Subsecription through Stripe Webook.
- Temporary IP blacklisting after multiple login failures.
- Distributed Caching with Redis for improved performance.
- Caching was utilized to reduce the database (postgres) hits, especially for organization roles and user current available organization roles. check cache services [cache services](./src/main/java/com/example/multitenant/services/cache).
- Distributed Locking was implemented using Redis, although it's currently not required in the application logic. It remains available for potential future use cases.
- [act-cli](https://github.com/nektos/act) was used to run github actions locally on docker before pushing to github.
- Supabase S3 buckets were utilized for image storage (users avatars & organizations images).

## Stripe Subscription Plans

Organizations can access different feature limits based on their active Stripe subscription tier. These tiers define how many members, roles, categories, and channels an organization can use.

| Plan             | Max Categories   | Max Roles   | Max Members   | Max Channels per Category  |
|:----------------:|:----------------:|:-----------:|:-------------:|:--------------------------:|
| **Free**         | 5                | 5           | 30            | 8                          |
| **Starter**      | 15               | 15          | 75            | 15                         |
| **Pro**          | 25               | 25          | 150           | 25                         |
| **Enterprise**   | 50               | 50          | 500           | 50                         |

## Installation
cloning the github repository is all what you need!
```
git clone https://github.com/MohammadAhmadKhader/SaaS-messaging-app
```
if you wish to use the scripts in [Makefile](./Makefile) you must install Make tool. if you are on windows check [Make for windows](https://gnuwin32.sourceforge.net/packages/make.htm).

you also have to install the [Stripe-cli](https://docs.stripe.com/stripe-cli/overview) for stripe make commands.

to use make commands run:
```
make [command]
```
ex:
```
make init-stripe
```