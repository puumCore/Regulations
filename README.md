# Regulations
 This is a regulations information management system for the KWS.

## Details
This is spring boot 3.0 microservice project which uses MongoDB as its datasource.

### Database
The database name is **kws_regulations_sys** which is **secured** with the **developer** & **kwsClient** roles.

![Database](https://user-images.githubusercontent.com/54445311/222440946-545a440e-c41d-4548-bdb5-0c0c555e599e.png)

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
      username: {
        bsonType: 'string'
      },
      password: {
        bsonType: 'string'
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

### 2. logs
Records of http listener audit events.

Expected document entry.
![Documents](https://user-images.githubusercontent.com/54445311/222441941-36433d03-d4ab-4a81-869d-ed4a565a7d8e.png)


Indexes used.
![Index](https://user-images.githubusercontent.com/54445311/222442111-47b88292-2409-426a-ad7a-71e8820c722d.png)


### 3. Visits
Records of customers who have visited the park.

Validation rules used.
![Validation](https://user-images.githubusercontent.com/54445311/219362125-a4806375-af37-43d7-b3c8-13f9192e1c45.png)

The following is the accurate description of **validation rules** used.

``` js
{
  $jsonSchema: {
    bsonType: 'object',
    required: [
      'timestamp',
      'session',
      'plates',
      'passengers',
      'phone',
      'account'
    ],
    properties: {
      timestamp: {
        bsonType: 'date'
      },
      session: {
        bsonType: 'string',
        'enum': [
          'MORNING',
          'EVENING',
          'FULL_DAY'
        ],
        description: 'Valid sessions are Morning, Evening & Full day. Check with developer on this'
      },
      plates: {
        bsonType: 'string'
      },
      passengers: {
        bsonType: 'int'
      },
      phone: {
        bsonType: 'string',
        description: 'The phone number should be a string'
      },
      account: {
        bsonType: 'object',
        required: [
          'name',
          'phone',
          'role',
          'username'
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
          username: {
            bsonType: 'string'
          }
        }
      }
    }
  },
  $and: [
    {
      timestamp: {
        $nin: [
          null
        ]
      },
      session: {
        $nin: [
          null,
          ''
        ]
      },
      plates: {
        $nin: [
          null,
          ''
        ]
      },
      passengers: {
        $nin: [
          null
        ],
        $gt: 0
      },
      phone: {
        $nin: [
          null,
          ''
        ]
      },
      account: {
        $nin: [
          null
        ]
      }
    }
  ]
}
````