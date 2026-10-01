# online-goods-store
-- baseUrl : http://localhost:1001 --<br>
-- The following test is executed using postman --
1. AUTHENTICATION (Super Admin)
   
| Field | Value |
|:-----|--------:|
| Method | POST |
| name | {{baseUrl}}/api/auth/login |
| Headers	Content-Type: | application/json |

Body (raw JSON):<br>
{<br>
  "email": "admin@store.com",<br>
  "password": "Admin123!"<br>
}
   
