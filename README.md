# Transit

It's an app for transit... specifically in Victoria, BC (where I live)

## Local dev

I open frontend in vscode

```
cd frontend
code .
```

and the backend in Idea

```
cd backend
idea .
```

To run the frontend, I use `npm run dev`. To run the backend, I run the main function in App.java. The frontend is configured to detect when its being run locally and to make requests to the backend at localhost:8080, otherwise it will allow the browser to make requests to the same hostname that the frontend is running on.
