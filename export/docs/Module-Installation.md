### Admin Security Changes

To maintain theme configurations in the Broadleaf admin, you will need to load new permissions. The recommended changes are located in the following files:

```
/config/bc/sql/load_export_admin_security.sql
```

> Note: In development, you can automatically load this SQL by adding this file to the `blPU.hibernate.hbm2ddl.import_files` property in the `development-shared.properties` file.