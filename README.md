## First steps
- Register at https://rawg.io/
- Get API Key

##Adding the API key to your request
You must include an API key with every request. In the following example, replace YOUR_API_KEY with your API key.

E.g.

GET https://api.rawg.io/api/platforms?key=YOUR_API_KEY

GET https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

## Complete API documentation
https://api.rawg.io/docs

## Initial implementation
- Game Platforms loading is partially implemented. Loaded only first page of data. To load all you need to pass all data pages (see next in PlatformDto class)
- you can type 'help' to get all available commands