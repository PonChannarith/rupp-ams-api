SELECT name
FROM app_roles ar
         INNER JOIN user_role ur ON ar.role_id = ur.role_id
WHERE user_id = 1;
