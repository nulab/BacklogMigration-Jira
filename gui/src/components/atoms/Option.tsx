import React from 'react';

interface Props {
  value: string;
}

export const Option: React.FC<Props> = ({ value }) => (
  <option value={value}>{value}</option>
);
