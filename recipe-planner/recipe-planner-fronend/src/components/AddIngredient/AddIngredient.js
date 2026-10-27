import React from 'react';
import PropTypes from 'prop-types';
import './AddIngredient.css';
import {Button, Col, Form, Row} from "react-bootstrap";

export const UNITS = ['PIECE', 'GRAMM', 'KILOGRAMM', 'LITRE', 'DECILITRE'];

const AddIngredient = ({ingredient, error, updateIngredient, removeIngredient}) => {

    const handleChange = (field) => (event) => {
        updateIngredient({...ingredient, [field]: event.target.value});
    };

    return (
        <Row>
            <Col>
                <Form.Group className="mb-1" controlId={`ingredient-name-${ingredient.key}`}>
                    <Form.Control
                        placeholder="Name"
                        aria-label="Ingredient name"
                        value={ingredient.name}
                        onChange={handleChange('name')}
                        isInvalid={!!error}
                    />
                    <Form.Control.Feedback type="invalid">{error}</Form.Control.Feedback>
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1" controlId={`ingredient-unit-${ingredient.key}`}>
                    <Form.Select aria-label="Unit" value={ingredient.unit} onChange={handleChange('unit')}>
                        {UNITS.map((unit) => <option key={unit} value={unit}>{unit}</option>)}
                    </Form.Select>
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1" controlId={`ingredient-amount-${ingredient.key}`}>
                    <Form.Control
                        type="number"
                        min="1"
                        placeholder="Quantity"
                        aria-label="Quantity"
                        value={ingredient.amount}
                        onChange={handleChange('amount')}
                        isInvalid={!!error}
                    />
                </Form.Group>
            </Col>
            <Col xs={1}>
                <Button
                    onClick={() => removeIngredient(ingredient)}
                    variant='outline-dark'
                    className="mb-1"
                    aria-label="Remove ingredient"
                >x</Button>
            </Col>
        </Row>
    )
}

AddIngredient.propTypes = {
    ingredient: PropTypes.shape({
        key: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        unit: PropTypes.oneOf(UNITS).isRequired,
        amount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    }).isRequired,
    error: PropTypes.string,
    updateIngredient: PropTypes.func.isRequired,
    removeIngredient: PropTypes.func.isRequired,
};

export default AddIngredient;
