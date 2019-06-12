import React from 'react';
import styled from '@emotion/styled';

interface OptionProps {
  value: string;
}

export const Option: React.FC<OptionProps> = ({ value }) => (
  <option value={value}>{value}</option>
);

const SelectStyled = styled.select(`
  outline: none;
  border: solid 1px #ccc;
  border-radius: 4px;
  padding: 8px 15px;
  font-size: 1rem;
  height: 2.2rem;
  cursor: pointer;

  &:focus {
    border-color: #4d90fe;
  }
`);

interface SelectProps {
  name: string;
  tabIndex: number;
}

export const Select: React.FC<SelectProps> = ({ name, tabIndex, children }) => (
  <SelectStyled name={name} tabIndex={tabIndex}>
    {children}
  </SelectStyled>
);
