# Regulations
 This is a regulations infomation management system for the KWS.

## Details
This is spring boot 3.0 microservice project which uses Mongo DB as its datasource.

### Database
The database name is **kws_regulations_sys** which is **secured** with the **developer** & **kwsClient** roles.

![Database](https://user-images.githubusercontent.com/54445311/219361705-3f114b6d-d60d-4f50-b0ba-65b9b8144c49.png)

The following is a description for the collections existing in the database as of now.

### 1. accounts
Primary use is for IAM features.

Expected document entry.
![Documents](https://user-images.githubusercontent.com/54445311/219361818-8c4ee133-a97c-4e82-a650-f975381afc9c.png)

Indexes used.
![Index](https://user-images.githubusercontent.com/54445311/219362021-be8f7b6e-2e80-4dd8-8466-b5fb01989a34.png)

Validation rules used.
![Validation](https://user-images.githubusercontent.com/54445311/219362125-a4806375-af37-43d7-b3c8-13f9192e1c45.png)

The following is the accurate description of **validation rules** used.

``` js
{
  $jsonSchema: {
    bsonType: 'object',
    required: [
      'name',
      'phone',
      'role',
      'username',
      'password',
      'isAuthenticated',
      'isAuthorised'
    ],
    properties: {
      name: {
        bsonType: 'string',
        description: 'The fullname should be a string'
      },
      phone: {
        bsonType: 'string',
        description: 'The phone number should be a string'
      },
      role: {
        bsonType: 'string',
        'enum': [
          'ADMIN',
          'GATE',
          'PATROL'
        ],
        description: 'Valid roles are Admin, Gate & Patrol. Check with developer on this'
      },
      isAuthenticated: {
        bsonType: 'bool',
        description: 'The authentication status should be a boolean'
      },
      isAuthorised: {
        bsonType: 'bool',
        description: 'The authorization status should be a boolean'
      }
    }
  },
  $and: [
    {
      name: {
        $nin: [
          null,
          ''
        ]
      },
      phone: {
        $nin: [
          null,
          ''
        ]
      },
      role: {
        $nin: [
          null,
          ''
        ]
      },
      username: {
        $nin: [
          null,
          ''
        ]
      },
      password: {
        $nin: [
          null,
          ''
        ]
      },
      isAuthenticated: {
        $nin: [
          null
        ]
      },
      isAuthorised: {
        $nin: [
          null
        ]
      }
    }
  ]
}
````

