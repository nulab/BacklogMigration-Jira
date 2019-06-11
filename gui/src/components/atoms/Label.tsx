import React from 'react';
import styled from '@emotion/styled';

const LabelStyled = styled.label(`
  display: block;
  font-size: 0.8rem;
  margin-bottom: 6px;
`);

interface Props {
  htmlFor?: string;
}

export const Label: React.FC<Props> = ({ htmlFor, children }) => (
  <LabelStyled {...{ htmlFor }}>{children}</LabelStyled>
);
