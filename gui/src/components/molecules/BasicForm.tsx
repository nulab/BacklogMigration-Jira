import React from 'react';
import { Row } from '../atoms/Row';
import { Label } from '../atoms/Label';
import { Input } from '../atoms/Input';
import { Select } from '../atoms/Select';
import { Option } from '../atoms/Option';
import styled from '@emotion/styled';

const BacklogUrlField = styled.div(`
  display: flex;
  align-items: center;
`);

const SpanStyled = styled.span(`
  flex: auto 0 0;
  padding: 0 10px;
`);

export const BasicForm = () => (
  <>
    <Row>
      <Label htmlFor='backlogUrl'>Backlog URL</Label>
      <BacklogUrlField>
        <SpanStyled>https://</SpanStyled>
        <Input
          id='backlogUrl'
          name='space'
          value=''
          tabIndex={1}
          className='space-id-field__id'
        />
        <Select name='domain' tabIndex={2}>
          <Option value='.backlog.com' />
          <Option value='.backlog.jp' />
          <Option value='.backlogtool.com' />
        </Select>
      </BacklogUrlField>
    </Row>
    <Row>
      <Label htmlFor='apiKey'>API Key</Label>
      <Input id='apiKey' name='apiKey' value='' tabIndex={3} />
    </Row>
    <Row>
      <Label htmlFor='projectKey'>Project Key</Label>
      <Input id='projectKey' name='projectKey' value='' tabIndex={4} />
    </Row>
  </>
);
