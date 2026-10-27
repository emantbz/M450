import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {Alert} from "react-bootstrap";
import RecipeForm from "../RecipeForm/RecipeForm";
import {getRecipe, updateRecipe} from "../../apis/recipeApi";

function EditRecipe() {
    const {recipeId} = useParams();
    const navigate = useNavigate();
    const [recipe, setRecipe] = useState(null);
    const [loadError, setLoadError] = useState(null);

    useEffect(() => {
        getRecipe(recipeId)
            .then((loaded) => loaded ? setRecipe(loaded) : setLoadError('Recipe not found.'))
            .catch(() => setLoadError('The recipe could not be loaded.'));
    }, [recipeId]);

    const handleSubmit = async (changedRecipe) => {
        await updateRecipe(recipeId, changedRecipe);
        navigate('/');
    };

    if (loadError) {
        return <Alert variant="danger" className="m-3">{loadError}</Alert>;
    }
    if (!recipe) {
        return <p className="m-3 text-white">Loading…</p>;
    }
    return <RecipeForm title="Edit Recipe" initialRecipe={recipe} onSubmit={handleSubmit}/>;
}

export default EditRecipe;
