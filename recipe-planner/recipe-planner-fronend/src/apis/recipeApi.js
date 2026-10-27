import axios from "axios";

const BASE_URL = "http://localhost:8080/api/recipes";

export const getRecipes = () => axios.get(BASE_URL).then((response) => response.data);

export const getRecipe = (recipeId) => axios.get(`${BASE_URL}/recipe/${recipeId}`).then((response) => response.data);

export const addRecipe = (recipe) => axios.post(BASE_URL, recipe).then((response) => response.data);

export const updateRecipe = (recipeId, recipe) =>
    axios.put(`${BASE_URL}/recipe/${recipeId}`, recipe).then((response) => response.data);
