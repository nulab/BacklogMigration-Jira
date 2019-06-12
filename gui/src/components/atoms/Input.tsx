import React from 'react';
import styled from '@emotion/styled';
import { Field, FieldProps } from 'formik';
import { Parameters } from '../../models/Prameter';

const InputStyled = styled.input(`
  outline: none;
  width: 100%;
  border: solid 1px #ccc;
  border-radius: 4px;
  padding: 8px 15px;
  font-size: 1rem;

  &:focus {
    border-color: #4d90fe;
  }
`);

interface Props {
  id: string;
  name: string;
  value: string;
  placeholder: string;
  tabIndex: number;
}

export const TextInput: React.FC<Props> = ({
  id,
  name,
  value,
  placeholder,
  tabIndex,
}) => (
  <Field
    type='text'
    id={id}
    name={name}
    value={value}
    placeholder={placeholder}
    tabIndex={tabIndex}
    render={({ field, form }: FieldProps<Parameters>) => (
      <InputStyled {...field} />
    )}
  />
);
