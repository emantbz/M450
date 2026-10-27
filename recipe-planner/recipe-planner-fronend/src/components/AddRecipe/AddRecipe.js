import React from 'react';
import {useNavigate} from "react-router-dom";
import RecipeForm from "../RecipeForm/RecipeForm";
import {addRecipe} from "../../apis/recipeApi";

function AddRecipe() {
    const navigate = useNavigate();

    const handleSubmit = async (recipe) => {
        await addRecipe(recipe);
        navigate('/');
    };

    return <RecipeForm title="Add Recipe" onSubmit={handleSubmit}/>;
}

export default AddRecipe;
